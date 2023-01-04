package com.example.torchwebsite.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.api.pojo.Donation;

public interface DonationService extends IService<Donation> {
    Page<Donation> getDonations(QueryWrapper<Donation> wrapper, Page<Donation> objectPage);

}
