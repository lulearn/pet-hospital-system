package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.Doctor;
import org.apache.ibatis.annotations.Mapper;

/**
 * 医生Mapper
 */
@Mapper
public interface DoctorMapper extends BaseMapper<Doctor> {
}
