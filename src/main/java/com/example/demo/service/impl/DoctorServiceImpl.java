package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.Doctor;
import com.example.demo.mapper.DoctorMapper;
import com.example.demo.service.DoctorService;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

@Service
public class DoctorServiceImpl extends ServiceImpl<DoctorMapper, Doctor> implements DoctorService {

    /**
     * 医生登录：密码MD5后匹配doctor表
     */
    @Override
    public Doctor login(String username, String password) {
        String md5 = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
        Doctor doctor = getOne(new LambdaQueryWrapper<Doctor>()
                .eq(Doctor::getUsername, username)
                .eq(Doctor::getPassword, md5));
        if (doctor == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        return doctor;
    }

    /**
     * 医生注册：校验用户名唯一性后入库
     */
    @Override
    public Doctor register(Doctor doctor) {
        Doctor exist = getOne(new LambdaQueryWrapper<Doctor>()
                .eq(Doctor::getUsername, doctor.getUsername()));
        if (exist != null) {
            throw new RuntimeException("用户名已存在");
        }
        doctor.setPassword(DigestUtils.md5DigestAsHex(doctor.getPassword().getBytes(StandardCharsets.UTF_8)));
        save(doctor);
        return doctor;
    }
}
