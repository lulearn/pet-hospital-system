package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.entity.*;
import com.example.demo.mapper.*;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 管理员端控制器 - 拥有系统最高权限
 * 管理用户、医生、药品、订单，以及个人中心
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private MedicineMapper medicineMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Autowired
    private ConsultationMapper consultationMapper;

    @Autowired
    private UserService userService;

    // ========== 用户管理 ==========

    @GetMapping("/users")
    public Result<List<User>> listUsers() {
        return Result.success(userMapper.selectList(null));
    }

    @PostMapping("/users")
    public Result<?> addUser(@RequestBody User user) {
        // 直接创建用户，保留管理员设置的role，不走register(会强制设为USER)
        user.setPassword(org.springframework.util.DigestUtils
                .md5DigestAsHex(user.getPassword().getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        userMapper.insert(user);
        return Result.success();
    }

    @PutMapping("/users")
    public Result<?> updateUser(@RequestBody User user) {
        // 如果传了密码则加密，没传则不修改密码
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(org.springframework.util.DigestUtils
                    .md5DigestAsHex(user.getPassword().getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        } else {
            user.setPassword(null); // null字段MyBatis-Plus不会更新
        }
        userMapper.updateById(user);
        return Result.success();
    }

    @DeleteMapping("/users/{id}")
    public Result<?> deleteUser(@PathVariable Long id) {
        userMapper.deleteById(id);
        return Result.success();
    }

    // ========== 医生管理 ==========

    @GetMapping("/doctors")
    public Result<List<Doctor>> listDoctors() {
        return Result.success(doctorMapper.selectList(null));
    }

    @PostMapping("/doctors")
    public Result<?> addDoctor(@RequestBody Doctor doctor) {
        doctor.setPassword(org.springframework.util.DigestUtils
                .md5DigestAsHex(doctor.getPassword().getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        doctorMapper.insert(doctor);
        return Result.success();
    }

    @PutMapping("/doctors")
    public Result<?> updateDoctor(@RequestBody Doctor doctor) {
        if (doctor.getPassword() != null && !doctor.getPassword().isEmpty()) {
            doctor.setPassword(org.springframework.util.DigestUtils
                    .md5DigestAsHex(doctor.getPassword().getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        } else {
            doctor.setPassword(null);
        }
        doctorMapper.updateById(doctor);
        return Result.success();
    }

    @DeleteMapping("/doctors/{id}")
    public Result<?> deleteDoctor(@PathVariable Long id) {
        doctorMapper.deleteById(id);
        return Result.success();
    }

    // ========== 药品管理 ==========

    @GetMapping("/medicines")
    public Result<List<Medicine>> listMedicines() {
        return Result.success(medicineMapper.selectList(null));
    }

    @PostMapping("/medicines")
    public Result<?> addMedicine(@RequestBody Medicine medicine) {
        medicineMapper.insert(medicine);
        return Result.success();
    }

    @PutMapping("/medicines")
    public Result<?> updateMedicine(@RequestBody Medicine medicine) {
        medicineMapper.updateById(medicine);
        return Result.success();
    }

    @DeleteMapping("/medicines/{id}")
    public Result<?> deleteMedicine(@PathVariable Long id) {
        medicineMapper.deleteById(id);
        return Result.success();
    }

    // ========== 订单管理 ==========

    /**
     * 查看所有订单
     */
    @GetMapping("/orders")
    public Result<List<Order>> listOrders() {
        return Result.success(orderMapper.selectList(null));
    }

    /**
     * 发货：只有已支付(PAID)状态的订单才能发货
     */
    @PutMapping("/orders/{id}/ship")
    public Result<?> shipOrder(@PathVariable Long id) {
        Order order = orderMapper.selectById(id);
        if (!"PAID".equals(order.getStatus())) {
            return Result.error("订单未支付，无法发货");
        }
        order.setStatus("SHIPPED");
        order.setShipTime(LocalDateTime.now());
        orderMapper.updateById(order);
        return Result.success();
    }

    /**
     * 删除订单：只能删除未支付或已收货的订单
     */
    @DeleteMapping("/orders/{id}")
    public Result<?> deleteOrder(@PathVariable Long id) {
        Order order = orderMapper.selectById(id);
        if (!"UNPAID".equals(order.getStatus()) && !"RECEIVED".equals(order.getStatus())) {
            return Result.error("只能删除未支付或已完成的订单");
        }
        orderMapper.deleteById(id);
        return Result.success();
    }

    // ========== 预约记录管理 ==========

    /**
     * 查看所有预约记录，含已删除的
     */
    @GetMapping("/appointments")
    public Result<List<Appointment>> listAppointments() {
        return Result.success(appointmentMapper.selectList(null));
    }

    /**
     * 软删除预约记录，不影响医生端统计数据
     */
    @DeleteMapping("/appointments/{id}")
    public Result<?> deleteAppointment(@PathVariable Long id) {
        Appointment appointment = appointmentMapper.selectById(id);
        if (appointment == null) {
            return Result.error("记录不存在");
        }
        appointment.setDeleted(1);
        appointmentMapper.updateById(appointment);
        return Result.success();
    }

    // ========== 就诊记录管理 ==========

    /**
     * 查看所有就诊记录，含已删除的（标记显示）
     */
    @GetMapping("/consultations")
    public Result<List<Consultation>> listConsultations() {
        return Result.success(consultationMapper.selectList(null));
    }

    /**
     * 软删除就诊记录，不影响医生端统计数据
     */
    @DeleteMapping("/consultations/{id}")
    public Result<?> deleteConsultation(@PathVariable Long id) {
        Consultation consultation = consultationMapper.selectById(id);
        if (consultation == null) {
            return Result.error("记录不存在");
        }
        consultation.setDeleted(1);
        consultationMapper.updateById(consultation);
        return Result.success();
    }

    // ========== 收入统计 ==========

    @GetMapping("/stats/revenue")
    public Result<Map<String, Object>> revenueStats() {
        List<Order> orders = orderMapper.selectList(null);
        BigDecimal totalRevenue = orders.stream()
                .filter(o -> !"UNPAID".equals(o.getStatus()))
                .map(Order::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long paidCount = orders.stream().filter(o -> "PAID".equals(o.getStatus())).count();
        long shippedCount = orders.stream().filter(o -> "SHIPPED".equals(o.getStatus())).count();
        long receivedCount = orders.stream().filter(o -> "RECEIVED".equals(o.getStatus())).count();
        return Result.success(Map.of("totalRevenue", totalRevenue, "paidCount", paidCount, "shippedCount", shippedCount, "receivedCount", receivedCount));
    }

    @GetMapping("/stats/dashboard")
    public Result<Map<String, Object>> dashboardStats() {
        long userCount = userMapper.selectCount(null);
        long doctorCount = doctorMapper.selectCount(null);
        long medicineCount = medicineMapper.selectCount(null);
        long orderCount = orderMapper.selectCount(null);
        long pendingAppointmentCount = appointmentMapper.selectCount(
                new LambdaQueryWrapper<Appointment>().eq(Appointment::getStatus, "PENDING").ne(Appointment::getDeleted, 1));
        // 月度收入
        List<Order> allOrders = orderMapper.selectList(null);
        BigDecimal monthRevenue = allOrders.stream()
                .filter(o -> o.getCreateTime() != null && o.getCreateTime().getMonthValue() == java.time.LocalDate.now().getMonthValue())
                .filter(o -> !"UNPAID".equals(o.getStatus()))
                .map(Order::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return Result.success(Map.of("userCount", userCount, "doctorCount", doctorCount, "medicineCount", medicineCount, "orderCount", orderCount, "pendingAppointmentCount", pendingAppointmentCount, "monthRevenue", monthRevenue));
    }

    // ========== 数据导出 ==========

    @GetMapping("/export/appointments")
    public Result<List<Map<String, Object>>> exportAppointments() {
        List<Appointment> list = appointmentMapper.selectList(null);
        return Result.success(list.stream().map(a -> {
            User u = userMapper.selectById(a.getUserId());
            Doctor d = doctorMapper.selectById(a.getDoctorId());
            return Map.<String, Object>of("id", a.getId(), "userName", u != null ? u.getRealName() : "", "doctorName", d != null ? d.getRealName() : "", "petName", a.getPetName(), "species", a.getSpecies() != null ? a.getSpecies() : "", "status", a.getStatus(), "appointmentTime", a.getAppointmentTime() != null ? a.getAppointmentTime().toString() : "", "createTime", a.getCreateTime() != null ? a.getCreateTime().toString() : "");
        }).collect(java.util.stream.Collectors.toList()));
    }

    @GetMapping("/export/orders")
    public Result<List<Map<String, Object>>> exportOrders() {
        List<Order> list = orderMapper.selectList(null);
        return Result.success(list.stream().map(o -> {
            User u = userMapper.selectById(o.getUserId());
            Medicine m = medicineMapper.selectById(o.getMedicineId());
            return Map.<String, Object>of("id", o.getId(), "userName", u != null ? u.getRealName() : "", "medicineName", m != null ? m.getName() : "", "quantity", o.getQuantity(), "totalPrice", o.getTotalPrice(), "status", o.getStatus(), "createTime", o.getCreateTime() != null ? o.getCreateTime().toString() : "");
        }).collect(java.util.stream.Collectors.toList()));
    }

    // ========== 个人中心 ==========

    @GetMapping("/profile")
    public Result<User> getProfile(HttpServletRequest request) {
        return Result.success(userService.getById((Long) request.getAttribute("userId")));
    }

    @PutMapping("/profile")
    public Result<?> updateProfile(@RequestBody User user, HttpServletRequest request) {
        user.setId((Long) request.getAttribute("userId"));
        user.setRole(null);
        user.setBalance(null);
        user.setPassword(null);
        userService.updateById(user);
        return Result.success();
    }

    /**
     * 修改密码
     */
    @PutMapping("/change-password")
    public Result<?> changePassword(@RequestBody Map<String, String> body, HttpServletRequest request) {
        User user = userService.getById((Long) request.getAttribute("userId"));
        String oldPwd = org.springframework.util.DigestUtils.md5DigestAsHex(body.get("oldPassword").getBytes(java.nio.charset.StandardCharsets.UTF_8));
        if (!user.getPassword().equals(oldPwd)) {
            return Result.error("旧密码错误");
        }
        user.setPassword(org.springframework.util.DigestUtils.md5DigestAsHex(body.get("newPassword").getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        userService.updateById(user);
        return Result.success();
    }
}
