package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.Consultation;
import org.apache.ibatis.annotations.Mapper;

/**
 * 就诊记录Mapper
 */
@Mapper
public interface ConsultationMapper extends BaseMapper<Consultation> {
}
