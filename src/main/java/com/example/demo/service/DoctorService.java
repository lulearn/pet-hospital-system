package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.entity.Doctor;

public interface DoctorService extends IService<Doctor> {
    Doctor login(String username, String password);
    Doctor register(Doctor doctor);
}
