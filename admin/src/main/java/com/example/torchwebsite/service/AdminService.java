package com.example.torchwebsite.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.api.pojo.Admin;

import java.util.List;

public interface AdminService extends IService<Admin> {
    List<Admin> getAdmins(QueryWrapper<Admin> wrapper);
    Admin getAdmin(Integer id);
    int insertAdmin(Admin admin);
    int deleteAdmin(Integer id);
    int updateAdmin(Admin admin);
}
