package com.example.torchwebsite.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.api.pojo.Volunteer;
import com.example.torchwebsite.mapper.VolunteerMapper;
import com.example.torchwebsite.service.VolunteerService;
import org.springframework.stereotype.Service;

@Service
public class VolunteerServiceImpl extends ServiceImpl<VolunteerMapper, Volunteer> implements VolunteerService {

}