package com.example.front.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.api.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {

}
