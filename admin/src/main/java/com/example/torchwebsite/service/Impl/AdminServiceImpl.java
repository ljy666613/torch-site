package com.example.torchwebsite.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.torchwebsite.mapper.AdminMapper;
import com.example.api.pojo.Admin;
import com.example.torchwebsite.service.AdminService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {
    @Resource
    private AdminMapper adminMapper;

    public Page<Admin> getAdmins(QueryWrapper<Admin> wrapper, Page<Admin> objectPage){
        return adminMapper.selectPage(objectPage,wrapper);
    }
    public Admin getAdmin(Integer id){
        return adminMapper.selectById(id);
    }

    @Override
    public int insertAdmin(Admin admin) {
        return adminMapper.insert(admin);
    }

    @Override
    public int deleteAdmin(Integer id) {
        return adminMapper.delete(new QueryWrapper<Admin>().eq("id",id));
    }

    @Override
    public int updateAdmin(Admin admin,QueryWrapper<Admin> wrapper) {
        return adminMapper.update(admin,wrapper);
    }

}
