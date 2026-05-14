package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.entity.*;
import com.example.demo.mapper.*;
import com.example.demo.entity.Consultation;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 用户端控制器 - 处理宠物主人的所有操作
 * 所有接口只能操作当前登录用户自己的数据
 */
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Autowired
    private MedicineMapper medicineMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ConsultationMapper consultationMapper;

    @Autowired
    private UserService userService;

    /**
     * 从JWT拦截器注入的request属性中获取当前用户ID
     */
    private Long getUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("userId");
    }

    // ========== 医生浏览 ==========

    @GetMapping("/doctors")
    public Result<List<Doctor>> listDoctors() {
        return Result.success(doctorMapper.selectList(null));
    }

    // ========== 预约管理 ==========

    /**
     * 查看自己的预约列表
     */
    @GetMapping("/appointments")
    public Result<List<Appointment>> listAppointments(HttpServletRequest request) {
        return Result.success(appointmentMapper.selectList(
                new LambdaQueryWrapper<Appointment>()
                        .eq(Appointment::getUserId, getUserId(request))
                        .ne(Appointment::getDeleted, 1)));
    }

    /**
     * 新增预约，状态默认为PENDING（待审核）
     */
    @PostMapping("/appointments")
    public Result<?> addAppointment(@RequestBody Appointment appointment, HttpServletRequest request) {
        appointment.setUserId(getUserId(request));
        appointment.setStatus("PENDING");
        appointmentMapper.insert(appointment);
        return Result.success();
    }

    /**
     * 修改预约，校验是否为本人预约
     */
    @PutMapping("/appointments")
    public Result<?> updateAppointment(@RequestBody Appointment appointment, HttpServletRequest request) {
        Appointment exist = appointmentMapper.selectById(appointment.getId());
        if (exist == null || !exist.getUserId().equals(getUserId(request))) {
            return Result.error("无权操作");
        }
        appointmentMapper.updateById(appointment);
        return Result.success();
    }

    /**
     * 删除预约，校验是否为本人预约
     */
    @DeleteMapping("/appointments/{id}")
    public Result<?> deleteAppointment(@PathVariable Long id, HttpServletRequest request) {
        Appointment exist = appointmentMapper.selectById(id);
        if (exist == null || !exist.getUserId().equals(getUserId(request))) {
            return Result.error("无权操作");
        }
        appointmentMapper.deleteById(id);
        return Result.success();
    }

    // ========== 药品浏览 ==========

    @GetMapping("/medicines")
    public Result<List<Medicine>> listMedicines() {
        return Result.success(medicineMapper.selectList(null));
    }

    // ========== 订单管理 ==========

    /**
     * 查看自己的订单列表
     */
    @GetMapping("/orders")
    public Result<List<Order>> listOrders(HttpServletRequest request) {
        return Result.success(orderMapper.selectList(
                new LambdaQueryWrapper<Order>().eq(Order::getUserId, getUserId(request))));
    }

    /**
     * 创建订单：校验库存是否充足，扣减库存后生成未支付订单
     */
    @PostMapping("/orders")
    public Result<?> addOrder(@RequestBody Order order, HttpServletRequest request) {
        Medicine medicine = medicineMapper.selectById(order.getMedicineId());
        if (medicine == null) {
            return Result.error("药品不存在");
        }
        if (medicine.getStock() < order.getQuantity()) {
            return Result.error("库存不足");
        }
        // 扣减库存
        medicine.setStock(medicine.getStock() - order.getQuantity());
        medicineMapper.updateById(medicine);

        order.setUserId(getUserId(request));
        order.setTotalPrice(medicine.getPrice().multiply(BigDecimal.valueOf(order.getQuantity())));
        order.setStatus("UNPAID");
        orderMapper.insert(order);
        return Result.success();
    }

    @PutMapping("/orders")
    public Result<?> updateOrder(@RequestBody Order order, HttpServletRequest request) {
        Order exist = orderMapper.selectById(order.getId());
        if (exist == null || !exist.getUserId().equals(getUserId(request))) {
            return Result.error("无权操作");
        }
        // 只允许修改数量，防止绕过支付修改状态/金额
        if (exist.getStatus() != null && !"UNPAID".equals(exist.getStatus())) {
            return Result.error("只能修改未支付订单");
        }
        exist.setQuantity(order.getQuantity());
        exist.setTotalPrice(order.getTotalPrice());
        orderMapper.updateById(exist);
        return Result.success();
    }

    @DeleteMapping("/orders/{id}")
    public Result<?> deleteOrder(@PathVariable Long id, HttpServletRequest request) {
        Order exist = orderMapper.selectById(id);
        if (exist == null || !exist.getUserId().equals(getUserId(request))) {
            return Result.error("无权操作");
        }
        orderMapper.deleteById(id);
        return Result.success();
    }

    /**
     * 支付订单：从余额扣款，订单状态变为PAID
     */
    @PostMapping("/orders/{id}/pay")
    public Result<?> payOrder(@PathVariable Long id, HttpServletRequest request) {
        Order order = orderMapper.selectById(id);
        if (order == null || !order.getUserId().equals(getUserId(request))) {
            return Result.error("无权操作");
        }
        User user = userService.getById(getUserId(request));
        if (user.getBalance().compareTo(order.getTotalPrice()) < 0) {
            return Result.error("余额不足");
        }
        user.setBalance(user.getBalance().subtract(order.getTotalPrice()));
        userService.updateById(user);
        order.setStatus("PAID");
        order.setPayTime(LocalDateTime.now());
        orderMapper.updateById(order);
        return Result.success();
    }

    /**
     * 确认收货，订单状态变为RECEIVED
     */
    @PostMapping("/orders/{id}/receive")
    public Result<?> receiveOrder(@PathVariable Long id, HttpServletRequest request) {
        Order order = orderMapper.selectById(id);
        if (order == null || !order.getUserId().equals(getUserId(request))) {
            return Result.error("无权操作");
        }
        order.setStatus("RECEIVED");
        order.setReceiveTime(LocalDateTime.now());
        orderMapper.updateById(order);
        return Result.success();
    }

    // ========== 就诊记录 ==========

    /**
     * 用户查看自己的就诊记录
     */
    @GetMapping("/consultations")
    public Result<List<Consultation>> listConsultations(HttpServletRequest request) {
        return Result.success(consultationMapper.selectList(
                new LambdaQueryWrapper<Consultation>().eq(Consultation::getUserId, getUserId(request))));
    }

    // ========== 充值 ==========

    @PostMapping("/recharge")
    public Result<?> recharge(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        BigDecimal amount = new BigDecimal(body.get("amount").toString());
        userService.recharge(getUserId(request), amount);
        return Result.success();
    }

    // ========== 个人中心 ==========

    @GetMapping("/user/profile")
    public Result<User> getProfile(HttpServletRequest request) {
        return Result.success(userService.getById(getUserId(request)));
    }

    /**
     * 修改个人信息，role和balance不允许自行修改，设为null跳过更新
     */
    @PutMapping("/user/profile")
    public Result<?> updateProfile(@RequestBody User user, HttpServletRequest request) {
        user.setId(getUserId(request));
        user.setRole(null);
        user.setBalance(null);
        user.setPassword(null);
        userService.updateById(user);
        return Result.success();
    }

    /**
     * 修改密码：校验旧密码正确后才能更新为新密码
     */
    @PutMapping("/user/change-password")
    public Result<?> changePassword(@RequestBody Map<String, String> body, HttpServletRequest request) {
        User user = userService.getById(getUserId(request));
        String oldPwd = org.springframework.util.DigestUtils.md5DigestAsHex(body.get("oldPassword").getBytes(java.nio.charset.StandardCharsets.UTF_8));
        if (!user.getPassword().equals(oldPwd)) {
            return Result.error("旧密码错误");
        }
        user.setPassword(org.springframework.util.DigestUtils.md5DigestAsHex(body.get("newPassword").getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        userService.updateById(user);
        return Result.success();
    }
}
