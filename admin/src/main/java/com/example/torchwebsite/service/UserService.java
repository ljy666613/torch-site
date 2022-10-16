package com.example.torchwebsite.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.api.pojo.User;

import java.util.List;

public interface UserService extends IService<User> {
    List<User> getUsers(QueryWrapper<User> wrapper);
    User getUser(Integer id);
    int insertUser(User user);
    int deleteUser(Integer id);
    int updateUser(User user,QueryWrapper<User> wrapper);
}
