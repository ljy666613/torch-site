package com.example.torchwebsite.controller;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.api.pojo.Admin;
import com.example.api.pojo.vo.Admin.AdminInfo;
import com.example.torchwebsite.service.AdminService;
import com.example.torchwebsite.utils.JwtUtil;
import com.example.torchwebsite.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Resource
    private AdminService adminService;

    private final JwtUtil jwtUtil ;

    @Autowired
    public AdminController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
//查询所有普通管理员
    @GetMapping
    public R<?> selectAdmins(HttpServletRequest request, HttpServletResponse response){
        Admin admin = (Admin) request.getAttribute("Admin");
        if(admin.getLevel() != 1){
            return R.error().message("没有root权限");
        }
        QueryWrapper<Admin> wrapper = new QueryWrapper<>();
        wrapper.ne("level",1);
        List<Admin> admins = adminService.getAdmins(wrapper);
        return R.ok().detail(admins);
    }
//添加普通管理员
    @PostMapping
    public R<?> insertAdmin(@RequestBody AdminInfo adminInfo, HttpServletRequest request, HttpServletResponse response){
        Admin admin = (Admin) request.getAttribute("Admin");
        if(admin.getLevel() != 1){
            return R.error().message("没有root权限");
        }

        QueryWrapper<Admin> wrapper = new QueryWrapper<>();
        wrapper.eq("name",adminInfo.getName());
        Admin selectedAdmin = adminService.getBaseMapper().selectOne(wrapper);
        if (selectedAdmin != null){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return R.error().message("用户名已存在");
        }

        Admin newAdmin = new Admin();
        newAdmin.setName(adminInfo.getName());
        newAdmin.setPassword(adminInfo.getPassword());

        int res = adminService.insertAdmin(newAdmin);
        if (res == 0){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return R.error().message("数据库添加失败");
        }
        return R.ok().message("");
    }
//删除普通管理员
    @DeleteMapping("/{admin_id}")
    public R<?> deleteAdmin(@PathVariable Integer admin_id,HttpServletRequest request, HttpServletResponse response){
        Admin admin = (Admin) request.getAttribute("Admin");
        if(admin.getLevel() != 1){
            return R.error().message("没有root权限");
        }
        int res = adminService.deleteAdmin(admin_id);
        if (res == 0){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return R.error().message("数据库删除失败");
        }
        return R.ok().message("");
    }
//    更新普通管理员信息   普通用户可以修改本人，而root用户只能修改自己
    @PutMapping("/{admin_id}")
    public R<?> updateAdmin(@RequestBody AdminInfo adminInfo, HttpServletRequest request, HttpServletResponse response, @PathVariable Integer admin_id){
        Admin admin = (Admin) request.getAttribute("Admin");
        if(admin.getLevel() != 1&&admin_id != admin.getId()){
            return R.error().message("没有root权限");
        }

        QueryWrapper<Admin> wrapper = new QueryWrapper<>();
        wrapper.eq("name",adminInfo.getName());
        Admin selectedAdmin = adminService.getBaseMapper().selectOne(wrapper);
        if (selectedAdmin != null){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return R.error().message("用户名已存在");
        }

        Admin newAdmin = new Admin();
        newAdmin.setName(adminInfo.getName());
        newAdmin.setPassword(adminInfo.getPassword());

        int res = adminService.updateAdmin(newAdmin,new QueryWrapper<Admin>().eq("id",admin_id));
        if (res == 0){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return R.error().message("更改信息失败");
        }
        return R.ok().message("");
    }
//登录接口
    @PostMapping("/login")
    public R<?> login(@RequestBody Admin admin, HttpServletRequest request){//这只是传进来的user，并没有在数据库中进行查找

        QueryWrapper<Admin> wrapper = new QueryWrapper<>();
        wrapper.eq("name",admin.getName()).eq("password",admin.getPassword());
        Admin res = adminService.getBaseMapper().selectOne(wrapper);
        if (res==null){
            return R.error().message("用户名或密码错误");
        }
        Map<String, Object> payload = new HashMap<>();
        payload.put("uid", res.getId());
        System.out.println("===="+payload.get("uid"));
        String token = jwtUtil.encodeToken(payload);
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("token", token);
//        return Result.success(jsonObject);
        return R.ok().detail(jsonObject);
    }
//    获取用户个人信息
    @GetMapping("/info")
    public R<?> getAdminLevel(HttpServletRequest request){
        Admin admin = (Admin) request.getAttribute("Admin");
        return R.ok().detail(admin);
    }



}
