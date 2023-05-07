package com.example.front.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.api.pojo.User;
import com.example.front.mapper.UserMapper;
import com.example.front.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private UserMapper userMapper;

    @Override
    public Page<User> getUsers(QueryWrapper<User> wrapper, Page<User>objectPage) {
        return userMapper.selectPage(objectPage,wrapper);
    }
    @Override
    public User getUser(Integer id) {
        return userMapper.selectById(id);
    }

    @Override
    public int insertUser(User user) {
        return userMapper.insert(user);
    }

    @Override
    public int deleteUser(Integer id) {
        return userMapper.deleteById(id);
    }

    @Override
    public int updateUser(User user,QueryWrapper<User> wrapper) {
        return userMapper.update(user,wrapper);
    }
}
