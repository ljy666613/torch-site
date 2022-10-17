package com.example.torchwebsite.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.api.pojo.Admin;


public interface AdminService extends IService<Admin> {
    Page<Admin> getAdmins(QueryWrapper<Admin> wrapper, Page<Admin> objectPage);
    Admin getAdmin(Integer id);
    int insertAdmin(Admin admin);
    int deleteAdmin(Integer id);
    int updateAdmin(Admin admin,QueryWrapper<Admin> wrapper);
}
