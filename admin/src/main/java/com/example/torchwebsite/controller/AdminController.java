package com.example.torchwebsite.controller;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.api.pojo.Admin;
import com.example.api.pojo.vo.Admin.AdminInfo;
import com.example.torchwebsite.service.AdminService;
import com.example.commen.utils.JwtUtil;
import com.example.commen.utils.R;
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

    @Autowired()
    public AdminController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
//查询所有普通管理员
    @GetMapping
    public R<?> selectAdmins(HttpServletRequest request, HttpServletResponse response,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             @RequestParam(defaultValue = "") String search){
        Admin admin = (Admin) request.getAttribute("Admin");
        if(admin.getLevel() != 1){
            return R.error().message("没有root权限");
        }
        QueryWrapper<Admin> wrapper = new QueryWrapper<>();
        wrapper.ne("level",1);
        wrapper.like("name",search);
        Page<Admin> admins = adminService.getAdmins(wrapper,new Page<>(pageNum, pageSize));
        List<Admin> records = admins.getRecords();
        long total = admins.getTotal();

        JSONObject jsonObject = new JSONObject();
        jsonObject.set("records",records);
        jsonObject.set("total",total);
        return R.ok().detail(jsonObject);
    }
//添加普通管理员
    @PostMapping
    public R<?> insertAdmin(@RequestBody AdminInfo adminInfo,
                            HttpServletRequest request, HttpServletResponse response){
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
//    更新普通管理员信息   普通用户可以修改自己，而root用户可以修改自己和普通管理员
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
        if (res.getIsActive() == 0){
            return R.error().message("账号已冻结");
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
//    获取管理员个人信息
    @GetMapping("/info")
    public R<?> getAdminLevel(HttpServletRequest request){
        Admin admin = (Admin) request.getAttribute("Admin");
        return R.ok().detail(admin);
    }
//  root获取指定管理员信息
    @GetMapping("/{admin_id}")
    public R<?> getAdmin(@PathVariable Integer admin_id,HttpServletRequest request){
        Admin adminRequest = (Admin) request.getAttribute("Admin");
        if(adminRequest.getLevel() != 1){
            return R.error().message("没有root权限");
        }
        Admin admin = adminService.getAdmin(admin_id);
        return R.ok().detail(admin);
    }
//  root更改管理员状态
    @PutMapping("/status/{admin_id}")
    public R<?> updateStatus(HttpServletRequest request, HttpServletResponse response, @PathVariable Integer admin_id){
        Admin admin = (Admin) request.getAttribute("Admin");
        if(admin.getLevel() != 1){
            return R.error().message("没有root权限");
        }
        if (admin_id == admin.getId()){
            return R.error().message("不能修改自己状态");
        }
        Admin selectedAdmin = adminService.getBaseMapper().selectOne(new QueryWrapper<Admin>().eq("id", admin_id));
        if (selectedAdmin == null){
            return R.error().message("该管理员不存在");
        }
        if (selectedAdmin.getIsActive() == 1){
            selectedAdmin.setIsActive(0);
        }else if (selectedAdmin.getIsActive() == 0){
            selectedAdmin.setIsActive(1);
        }
        int res = adminService.getBaseMapper().updateById(selectedAdmin);

        if (res == 0){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return R.error().message("更改信息失败");
        }

        return R.ok().message("");


    }

}
