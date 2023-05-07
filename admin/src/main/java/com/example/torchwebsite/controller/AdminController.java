package com.example.torchwebsite.controller;

import cn.hutool.json.JSONObject;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.api.constants.AdminConstants;
import com.example.api.pojo.Activity;
import com.example.api.pojo.Admin;
import com.example.api.pojo.User;
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
import java.io.IOException;
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

    @Resource
    private ElasticsearchClient client;

    private Integer countTest = 0;

    private List<Integer> list = new ArrayList<>();

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
//        Admin(id=16, name=ljy22123, password=665544332211, level=null, isActive=null, isLogin=null)
//        System.out.println(newAdmin);

//        insert 的 newAdmin 自动获取id...... 所有可以直接getId()
        rabbitTemplate.convertAndSend(AdminConstants.ADMIN_EXCHANGE,AdminConstants.ADMIN_INSERT_KEY,newAdmin.getId());
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
        rabbitTemplate.convertAndSend(AdminConstants.ADMIN_EXCHANGE,AdminConstants.ADMIN_DELETE_KEY,admin_id);
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
        wrapper.ne("id",admin_id);
        Admin selectedAdmin = adminService.getBaseMapper().selectOne(wrapper);
        if (selectedAdmin != null){
            return R.error().message("用户名已存在");
        }

        Admin newAdmin = new Admin();
        newAdmin.setId(admin_id);
        newAdmin.setName(adminInfo.getName());
        newAdmin.setPassword(adminInfo.getPassword());

        int res = adminService.updateAdmin(newAdmin,new QueryWrapper<Admin>().eq("id",admin_id));
        if (res == 0){
            return R.error().message("更改信息失败");
        }
        rabbitTemplate.convertAndSend(AdminConstants.ADMIN_EXCHANGE,AdminConstants.ADMIN_INSERT_KEY,newAdmin.getId());
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
        jsonObject.set("id",res.getId());

//
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

//    定时
    @Scheduled(cron = "*/5 * * * * ?")
    public void Listen(){

//        System.out.println("================"+countTest++);
//        arraylist 的循环删除问题，正向循环删除会有bug，如果有两个连续的数据都符合删除的条件，
//        那么，因删除第一个后重新编制索引，所以第二个根本就不会进入循环
//        总结：for循环正向删除，会遗漏连续重复的元素。
//        而，反向不会跳过元素！
//        forEach循环会直接报错~
        for (int i = list.size() - 1; i >= 0; i--) {
            Admin admin = adminService.getBaseMapper().selectOne(new QueryWrapper<Admin>().eq("id", list.get(i)));
            if (redisUtil.get("LOGIN_ID:"+list.get(i)) == null){
                admin.setIsLogin(0);
                adminService.getBaseMapper().updateById(admin);
                list.remove(i);
            }
        }
//        if (redisUtil.get("LOGIN_ID:"+admin.getId()) == null) {
//            return R.error().message("");
//        }
//        admin.setIsLogin(0);
//        adminService.getBaseMapper().updateById(admin);
    }
    @GetMapping("/time")
    public R<?> Time(HttpServletRequest request){
        Admin admin = (Admin) request.getAttribute("Admin");
        redisUtil.set("LOGIN_ID:"+admin.getId(),admin,5);
        list.add(admin.getId());
        admin.setIsLogin(1);
        adminService.getBaseMapper().updateById(admin);
//        测试罢了：
//        new LoginEndPoint().onOpen((Session) request.getSession(), new EndpointConfig() {
//            @Override
//            public List<Class<? extends Encoder>> getEncoders() {
//                return null;
//            }
//
//            @Override
//            public List<Class<? extends Decoder>> getDecoders() {
//                return null;
//            }
//
//            @Override
//            public Map<String, Object> getUserProperties() {
//                return null;
//            }
//        });
//        LoginEndPoint.sendInfo("msg","ok");
        return R.ok().message("");
    }

    @PostMapping("/exit")
    public R<?> exit(HttpServletRequest request){
        Admin admin = (Admin) request.getAttribute("Admin");
        admin.setIsLogin(0);
        adminService.getBaseMapper().updateById(admin);
        return R.ok().message("");
    }

//    admin  match查询其他admin信息
    @GetMapping("/elasticsearch")
    public R<?> EsSearch(HttpServletRequest request,
//                         ES与Page查询不同，from：从第几项开始，size查询多少项
//                         而pageSize：页面大小，pageNum：页号

                         @RequestParam(defaultValue = "1") Integer from,
                         @RequestParam(defaultValue = "10") Integer size,
                         @RequestParam(defaultValue = "") String searchContent){
        Admin admin = (Admin) request.getAttribute("Admin");
        SearchResponse<Admin> search = null;
        try {
            search = client.search(s -> s
    //                  可以 多索引表查询
                            .index("torchwebsite-admin" /*,"products"*/).size(size).from(from)
                            .query(q -> q.match(m -> m
                                    .field("all")
                                    .query(searchContent)
                            ))
                ,Admin.class);
            } catch (IOException e) {
                e.printStackTrace();
        }

        List<Admin> source = new ArrayList<>();
        for (Hit<Admin> hit: search.hits().hits()) {
            System.out.println(hit.source());
            source.add(hit.source());
        }
        return R.ok().detail(source);
    }

//    管理员 match查询所有相关信息

//    暂未找到方便多索引查询的api......所以只能把两个分开了......
//    也可以直接：http://127.0.0.1:9200/torchwebsite-admin,torchwebsite-user/_search
    @GetMapping("/elasticsearch/searchAll")
    public R<?> searchAll(HttpServletRequest request,
                               @RequestParam(defaultValue = "1") Integer from,
                               @RequestParam(defaultValue = "10") Integer size,
                               @RequestParam(defaultValue = "") String searchContent){
        Admin admin = (Admin) request.getAttribute("Admin");
        SearchResponse<Admin> search = null;
        try {
            search = client.search(s -> s
                            //
                            .index("torchwebsite-admin").size(size).from(from)
                            .query(q -> q.match(m -> m
                                    .field("all")
                                    .query(searchContent)
                            ))
                    ,Admin.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Admin> source = new ArrayList<>();
        for (Hit<Admin> hit: search.hits().hits()) {
            System.out.println(hit.source());
            source.add(hit.source());
        }
        SearchResponse<User> searchUser = null;
        try {
            searchUser = client.search(s -> s
                            //
                            .index("torchwebsite-user").size(size).from(from)
                            .query(q -> q.match(m -> m
                                    .field("all")
                                    .query(searchContent)
                            ))
                    ,User.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<User> sourceUser = new ArrayList<>();
        for (Hit<User> hit: searchUser.hits().hits()) {
            System.out.println(hit.source());
            sourceUser.add(hit.source());
        }
        SearchResponse<Activity> searchAct = null;
        try {
            searchAct = client.search(s -> s
                            //
                            .index("torchwebsite-activity").size(size).from(from)
                            .query(q -> q.match(m -> m
                                    .field("all")
                                    .query(searchContent)
                            ))
                    ,Activity.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Activity> sourceAct = new ArrayList<>();
        for (Hit<Activity> hit: searchAct.hits().hits()) {
            System.out.println(hit.source());
            sourceAct.add(hit.source());
        }
        JSONObject jsonObject = new JSONObject();

        jsonObject.set("admin",source);
        jsonObject.set("user",sourceUser);
        jsonObject.set("activity",sourceAct);

        return R.ok().detail(jsonObject);
    }

}
