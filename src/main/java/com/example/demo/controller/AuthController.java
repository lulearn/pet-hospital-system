package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.common.Result;
import com.example.demo.entity.Doctor;
import com.example.demo.entity.User;
import com.example.demo.mapper.DoctorMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.DoctorService;
import com.example.demo.service.UserService;
import com.example.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器 - 处理登录和注册
 * 登录：验证账号密码，生成JWT令牌返回
 * 注册：支持用户和医生两种角色注册
 */
@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 登录接口，根据role区分查询user表或doctor表
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String role = body.get("role");

        if ("DOCTOR".equals(role)) {
            // 医生登录：查doctor表
            Doctor doctor = doctorService.login(username, password);
            String token = jwtUtil.generateToken(doctor.getId(), doctor.getUsername(), "DOCTOR");
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", doctor);
            data.put("role", "DOCTOR");
            return Result.success(data);
        } else {
            // 用户/管理员登录：查user表
            User user = userService.login(username, password);
            String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", user);
            data.put("role", user.getRole());
            return Result.success(data);
        }
    }

    /**
     * 注册接口，管理员只能由系统预设，不开放注册
     */
    @PostMapping("/register")
    public Result<?> register(@RequestBody Map<String, Object> body) {
        String role = (String) body.get("role");
        if ("DOCTOR".equals(role)) {
            Doctor doctor = new Doctor();
            doctor.setUsername((String) body.get("username"));
            doctor.setPassword((String) body.get("password"));
            doctor.setRealName((String) body.get("realName"));
            doctor.setPhone((String) body.get("phone"));
            doctor.setEmail((String) body.get("email"));
            doctor.setSpecialty((String) body.get("specialty"));
            doctorService.register(doctor);
        } else {
            User user = new User();
            user.setUsername((String) body.get("username"));
            user.setPassword((String) body.get("password"));
            user.setRealName((String) body.get("realName"));
            user.setPhone((String) body.get("phone"));
            user.setEmail((String) body.get("email"));
            user.setRole("ADMIN".equals(role) ? "ADMIN" : "USER");
            userService.register(user);
        }
        return Result.success();
    }

    /**
     * 忘记密码：通过用户名+手机号验证后重置密码
     */
    @PostMapping("/reset-password")
    public Result<?> resetPassword(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String phone = body.get("phone");
        String newPassword = body.get("newPassword");
        // 先查医生表
        Doctor doctor = doctorMapper.selectOne(new LambdaQueryWrapper<Doctor>().eq(Doctor::getUsername, username));
        if (doctor != null && phone.equals(doctor.getPhone())) {
            doctor.setPassword(DigestUtils.md5DigestAsHex(newPassword.getBytes(StandardCharsets.UTF_8)));
            doctorMapper.updateById(doctor);
            return Result.success();
        }
        // 再查用户表
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user != null && phone.equals(user.getPhone())) {
            user.setPassword(DigestUtils.md5DigestAsHex(newPassword.getBytes(StandardCharsets.UTF_8)));
            userMapper.updateById(user);
            return Result.success();
        }
        return Result.error("用户名或手机号不匹配");
    }
}
