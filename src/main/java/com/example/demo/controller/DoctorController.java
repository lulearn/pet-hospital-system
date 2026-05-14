package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.common.Result;
import com.example.demo.entity.Appointment;
import com.example.demo.entity.Consultation;
import com.example.demo.entity.Doctor;
import com.example.demo.entity.User;
import com.example.demo.mapper.AppointmentMapper;
import com.example.demo.mapper.ConsultationMapper;
import com.example.demo.mapper.DoctorMapper;
import com.example.demo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 医生端控制器 - 处理医生的预约审核和就诊记录
 * 所有接口只能操作该医生自己的数据
 */
@RestController
@RequestMapping("/api/doctor")
public class DoctorController {

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Autowired
    private ConsultationMapper consultationMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DoctorMapper doctorMapper;

    private Long getDoctorId(HttpServletRequest request) {
        return (Long) request.getAttribute("userId");
    }

    // ========== 预约管理 ==========

    /**
     * 查看分配给自己的预约列表（不含已删除）
     */
    @GetMapping("/appointments")
    public Result<List<Appointment>> listAppointments(HttpServletRequest request) {
        return Result.success(appointmentMapper.selectList(
                new LambdaQueryWrapper<Appointment>()
                        .eq(Appointment::getDoctorId, getDoctorId(request))
                        .ne(Appointment::getDeleted, 1)));
    }

    /**
     * 预约统计：待处理只计未删除的，已完成为累加（含已删除）
     */
    @GetMapping("/appointments/stats")
    public Result<Map<String, Long>> appointmentStats(HttpServletRequest request) {
        Long doctorId = getDoctorId(request);
        List<Appointment> all = appointmentMapper.selectList(
                new LambdaQueryWrapper<Appointment>().eq(Appointment::getDoctorId, doctorId));
        // 待处理：只统计未删除的，处理掉就会减少
        long pending = all.stream().filter(a -> "PENDING".equals(a.getStatus()) && a.getDeleted() != 1).count();
        // 已完成：含已删除的，累加不减少
        long completed = all.stream().filter(a -> "COMPLETED".equals(a.getStatus())).count();
        return Result.success(Map.of("pending", pending, "completed", completed));
    }

    @PostMapping("/appointments")
    public Result<?> addAppointment(@RequestBody Appointment appointment, HttpServletRequest request) {
        appointment.setDoctorId(getDoctorId(request));
        appointment.setStatus("PENDING");
        appointmentMapper.insert(appointment);
        return Result.success();
    }

    /**
     * 修改预约（通过/拒绝在此操作中处理）
     */
    @PutMapping("/appointments")
    public Result<?> updateAppointment(@RequestBody Appointment appointment, HttpServletRequest request) {
        Appointment exist = appointmentMapper.selectById(appointment.getId());
        if (exist == null || !exist.getDoctorId().equals(getDoctorId(request))) {
            return Result.error("无权操作");
        }
        appointmentMapper.updateById(appointment);
        return Result.success();
    }

    @DeleteMapping("/appointments/{id}")
    public Result<?> deleteAppointment(@PathVariable Long id, HttpServletRequest request) {
        Appointment exist = appointmentMapper.selectById(id);
        if (exist == null || !exist.getDoctorId().equals(getDoctorId(request))) {
            return Result.error("无权操作");
        }
        appointmentMapper.deleteById(id);
        return Result.success();
    }

    // ========== 就诊管理 ==========

    /**
     * 查看自己的就诊记录列表（不含已删除）
     */
    @GetMapping("/consultations")
    public Result<List<Consultation>> listConsultations(HttpServletRequest request) {
        return Result.success(consultationMapper.selectList(
                new LambdaQueryWrapper<Consultation>()
                        .eq(Consultation::getDoctorId, getDoctorId(request))
                        .ne(Consultation::getDeleted, 1)));
    }

    /**
     * 就诊统计（含已删除记录，保证累加不减少）
     */
    @GetMapping("/consultations/stats")
    public Result<Map<String, Long>> consultationStats(HttpServletRequest request) {
        Long doctorId = getDoctorId(request);
        List<Consultation> all = consultationMapper.selectList(
                new LambdaQueryWrapper<Consultation>().eq(Consultation::getDoctorId, doctorId));
        long total = all.size();
        long completed = all.stream().filter(c -> "COMPLETED".equals(c.getStatus())).count();
        return Result.success(Map.of("total", total, "completed", completed));
    }

    /**
     * 添加就诊记录，状态为就诊中，同步更新关联预约状态
     */
    @PostMapping("/consultations")
    public Result<?> addConsultation(@RequestBody Consultation consultation, HttpServletRequest request) {
        consultation.setDoctorId(getDoctorId(request));
        consultation.setStatus("IN_PROGRESS");
        consultationMapper.insert(consultation);
        // 将关联预约状态同步为就诊中
        if (consultation.getAppointmentId() != null) {
            Appointment appt = appointmentMapper.selectById(consultation.getAppointmentId());
            if (appt != null && appt.getDoctorId().equals(getDoctorId(request))) {
                appt.setStatus("IN_PROGRESS");
                appointmentMapper.updateById(appt);
            }
        }
        return Result.success();
    }

    /**
     * 完成就诊：将就诊记录和关联预约都标记为已完成
     */
    @PutMapping("/consultations/{id}/complete")
    public Result<?> completeConsultation(@PathVariable Long id, HttpServletRequest request) {
        Consultation consultation = consultationMapper.selectById(id);
        if (consultation == null || !consultation.getDoctorId().equals(getDoctorId(request))) {
            return Result.error("无权操作");
        }
        consultation.setStatus("COMPLETED");
        consultationMapper.updateById(consultation);
        // 将关联预约也标记为已完成
        if (consultation.getAppointmentId() != null) {
            Appointment appt = appointmentMapper.selectById(consultation.getAppointmentId());
            if (appt != null && appt.getDoctorId().equals(getDoctorId(request))) {
                appt.setStatus("COMPLETED");
                appointmentMapper.updateById(appt);
            }
        }
        return Result.success();
    }

    @PutMapping("/consultations")
    public Result<?> updateConsultation(@RequestBody Consultation consultation, HttpServletRequest request) {
        Consultation exist = consultationMapper.selectById(consultation.getId());
        if (exist == null || !exist.getDoctorId().equals(getDoctorId(request))) {
            return Result.error("无权操作");
        }
        consultationMapper.updateById(consultation);
        return Result.success();
    }

    @DeleteMapping("/consultations/{id}")
    public Result<?> deleteConsultation(@PathVariable Long id, HttpServletRequest request) {
        Consultation exist = consultationMapper.selectById(id);
        if (exist == null || !exist.getDoctorId().equals(getDoctorId(request))) {
            return Result.error("无权操作");
        }
        consultationMapper.deleteById(id);
        return Result.success();
    }

    // ========== 个人中心 ==========

    @GetMapping("/profile")
    public Result<Doctor> getProfile(HttpServletRequest request) {
        return Result.success(doctorMapper.selectById(getDoctorId(request)));
    }

    @PutMapping("/profile")
    public Result<?> updateProfile(@RequestBody Doctor doctor, HttpServletRequest request) {
        doctor.setId(getDoctorId(request));
        doctor.setPassword(null);
        doctorMapper.updateById(doctor);
        return Result.success();
    }

    /**
     * 查询所有用户（供医生查看预约时显示用户名）
     */
    @GetMapping("/users")
    public Result<List<User>> listUsers() {
        return Result.success(userMapper.selectList(null));
    }

    /**
     * 修改密码：校验旧密码正确后才能更新
     */
    @PutMapping("/change-password")
    public Result<?> changePassword(@RequestBody Map<String, String> body, HttpServletRequest request) {
        Doctor doctor = doctorMapper.selectById(getDoctorId(request));
        String oldPwd = org.springframework.util.DigestUtils.md5DigestAsHex(body.get("oldPassword").getBytes(java.nio.charset.StandardCharsets.UTF_8));
        if (!doctor.getPassword().equals(oldPwd)) {
            return Result.error("旧密码错误");
        }
        doctor.setPassword(org.springframework.util.DigestUtils.md5DigestAsHex(body.get("newPassword").getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        doctorMapper.updateById(doctor);
        return Result.success();
    }
}
