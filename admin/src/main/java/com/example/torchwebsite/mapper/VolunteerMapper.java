package com.example.torchwebsite.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.api.pojo.Volunteer;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VolunteerMapper extends BaseMapper<Volunteer> {
}
