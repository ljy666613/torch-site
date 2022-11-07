package com.example.torchwebsite.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.api.pojo.Activity;
import com.example.torchwebsite.mapper.ActivityMapper;
import com.example.torchwebsite.service.ActivityService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements ActivityService {
    @Resource
    private ActivityMapper activityMapper;
    @Override
    public Page<Activity> getActivities(QueryWrapper<Activity> wrapper, Page<Activity> objectPage) {
        return activityMapper.selectPage(objectPage,wrapper);
    }
}
