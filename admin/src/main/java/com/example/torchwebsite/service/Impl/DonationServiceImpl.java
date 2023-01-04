package com.example.torchwebsite.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.api.pojo.Donation;
import com.example.api.pojo.Donation;
import com.example.torchwebsite.mapper.DonationMapper;
import com.example.torchwebsite.mapper.DonationMapper;
import com.example.torchwebsite.service.DonationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class DonationServiceImpl extends ServiceImpl<DonationMapper, Donation> implements DonationService {
    @Resource
    private DonationMapper donationMapper;
    @Override
    public Page<Donation> getDonations(QueryWrapper<Donation> wrapper, Page<Donation> objectPage) {
        return donationMapper.selectPage(objectPage,wrapper);
    }
}
