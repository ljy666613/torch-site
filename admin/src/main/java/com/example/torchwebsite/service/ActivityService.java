package com.example.torchwebsite.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.api.pojo.Activity;

public interface ActivityService extends IService<Activity> {

    Page<Activity> getActivities(QueryWrapper<Activity> wrapper, Page<Activity> objectPage);
}
