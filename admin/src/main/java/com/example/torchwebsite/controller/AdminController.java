package com.example.torchwebsite.controller;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.api.constants.AdminConstants;
import com.example.api.pojo.Admin;
import com.example.api.pojo.vo.Admin.AdminInfo;
import com.example.torchwebsite.service.AdminService;
import com.example.torchwebsite.utils.JwtUtil;
import com.example.torchwebsite.utils.R;
import com.example.torchwebsite.utils.RedisUtil;
//import com.example.torchwebsite.ws.LoginEndPoint;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.Decoder;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private AdminService adminService;

    private RedisUtil redisUtil;

    private final JwtUtil jwtUtil ;

    private RedissonClient redissonClient;

//    private Integer countTest = 0;

//    private List<Integer> list = new ArrayList<>();

    @Autowired
    public AdminController(JwtUtil jwtUtil,RedissonClient redissonClient,RedisUtil redisUtil) {
        this.jwtUtil = jwtUtil;
        this.redissonClient = redissonClient;
        this.redisUtil = redisUtil;
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
            return R.error().message("用户名已存在");
        }

        Admin newAdmin = new Admin();
        newAdmin.setName(adminInfo.getName());
        newAdmin.setPassword(adminInfo.getPassword());

        int res = adminService.insertAdmin(newAdmin);
        if (res == 0){
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
            return R.error().message("用户名已存在");
        }

        Admin newAdmin = new Admin();
        newAdmin.setId(admin_id);
//        newAdmin.setName(adminInfo.getName());
        newAdmin.setPassword(adminInfo.getPassword());

        int res = adminService.updateAdmin(newAdmin,new QueryWrapper<Admin>().eq("id",admin_id));
        if (res == 0){
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
        if (res.getIsLogin() == 1){
            return R.error().message("账号已登录");
        }
        res.setIsLogin(1);
        int i = adminService.getBaseMapper().updateById(res);
        if (i == 0){
            return R.error().message("");
        }
        Map<String, Object> payload = new HashMap<>();
        payload.put("uid", res.getId());

        System.out.println("===="+payload.get("uid"));
        String token = jwtUtil.encodeToken(payload);
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("level",res.getLevel());
        jsonObject.set("token", token);

//
        return R.ok().detail(jsonObject);
    }
//    获取管理员个人信息
    @GetMapping("/info")
    public R<?> getAdminLevel(HttpServletRequest request){
        Admin admin = (Admin) request.getAttribute("Admin");
        rabbitTemplate.convertAndSend(AdminConstants.ADMIN_EXCHANGE,AdminConstants.ADMIN_INSERT_KEY,admin.getId());
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
            return R.error().message("更改信息失败");
        }

        return R.ok().message("");


    }
//    退出接口
    @PutMapping("/exit")
    public R<?> updateIsLogin(HttpServletRequest request){
        Admin admin = (Admin) request.getAttribute("Admin");
        admin.setIsLogin(0);
        adminService.getBaseMapper().updateById(admin);
        return R.ok().message("");
    }

////    定时
//    @Scheduled(cron = "*/5 * * * * ?")
//    public void Listen(){
//
////        System.out.println("================"+countTest++);
////        arraylist 的循环删除问题，正向循环删除会有bug，如果有两个连续的数据都符合删除的条件，
////        那么，因删除第一个后重新编制索引，所以第二个根本就不会进入循环
////        总结：for循环正向删除，会遗漏连续重复的元素。
////        而，反向不会跳过元素！
////        forEach循环会直接报错~
//        for (int i = list.size() - 1; i >= 0; i--) {
//            Admin admin = adminService.getBaseMapper().selectOne(new QueryWrapper<Admin>().eq("id", list.get(i)));
//            if (redisUtil.get("LOGIN_ID:"+list.get(i)) == null){
//                admin.setIsLogin(0);
//                adminService.getBaseMapper().updateById(admin);
//                list.remove(i);
//            }
//        }
////        if (redisUtil.get("LOGIN_ID:"+admin.getId()) == null) {
////            return R.error().message("");
////        }
////        admin.setIsLogin(0);
////        adminService.getBaseMapper().updateById(admin);
//    }
//    @GetMapping("/time")
//    public R<?> Time(HttpServletRequest request){
//        Admin admin = (Admin) request.getAttribute("Admin");
//        redisUtil.set("LOGIN_ID:"+admin.getId(),admin,5);
//        list.add(admin.getId());
//        admin.setIsLogin(1);
//        adminService.getBaseMapper().updateById(admin);
////        测试罢了：
////        new LoginEndPoint().onOpen((Session) request.getSession(), new EndpointConfig() {
////            @Override
////            public List<Class<? extends Encoder>> getEncoders() {
////                return null;
////            }
////
////            @Override
////            public List<Class<? extends Decoder>> getDecoders() {
////                return null;
////            }
////
////            @Override
////            public Map<String, Object> getUserProperties() {
////                return null;
////            }
////        });
////        LoginEndPoint.sendInfo("msg","ok");
//        return R.ok().message("");
//    }

    @PostMapping("/exit")
    public R<?> exit(HttpServletRequest request){
        Admin admin = (Admin) request.getAttribute("Admin");
        admin.setIsLogin(0);
        adminService.getBaseMapper().updateById(admin);
        return R.ok().message("");
    }

}
