package com.example.front.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.api.pojo.User;

import java.util.List;

public interface UserService extends IService<User> {
    Page<User> getUsers(QueryWrapper<User> wrapper, Page<User> objectPage);
    User getUser(Integer id);
    int insertUser(User user);
    int deleteUser(Integer id);
    int updateUser(User user,QueryWrapper<User> wrapper);
}
