package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.Medicine;
import org.apache.ibatis.annotations.Mapper;

/**
 * 药品Mapper
 */
@Mapper
public interface MedicineMapper extends BaseMapper<Medicine> {
}
