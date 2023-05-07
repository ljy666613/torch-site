package com.example.front.controller;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.api.pojo.Admin;
import com.example.api.pojo.News;
import com.example.api.pojo.vo.User.CodeCheck;
import com.example.api.pojo.vo.User.RegisterCodeCheck;
import com.example.api.pojo.vo.User.UserInfo;
//import com.example.commen.utils.*;
//
//import com.example.commen.utils.inter.CacheCode;
//import com.example.commen.utils.inter.ResultCode;
import com.example.front.service.NewsService;
import com.example.front.service.UserService;
//import lombok.Value;
import com.example.front.utils.*;
import com.example.front.utils.inter.CacheCode;
import com.example.front.utils.inter.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import com.example.api.pojo.User;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.web.client.RestTemplate;

import static java.awt.Desktop.Action.MAIL;

@RestController
@RequestMapping("/api/front")
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private NewsService newsService;

    private final JwtUtil jwtUtil ;
    private final JudgeCookieToken judgeCookieToken;
    private final EmailSendUtil emailSendUtil;
    private RedissonClient redissonClient;

    private RedisUtil redisUtil;

    private TokenUtil tokenUtil;
    @Value("${spring.mail.username}")
    private String MAIL;
    @Autowired()
    public UserController(JwtUtil jwtUtil,RedissonClient client,JudgeCookieToken judgeCookieToken,EmailSendUtil emailSendUtil,RedisUtil redisUtil,
                          TokenUtil tokenUtil){
        this.jwtUtil = jwtUtil;
        this.redissonClient = client;
        this.judgeCookieToken = judgeCookieToken;
        this.emailSendUtil = emailSendUtil;
        this.redisUtil = redisUtil;
        this.tokenUtil = tokenUtil;
    }
//登录接口
    @PostMapping("/login")
    public R<?> login(@RequestBody User user, HttpServletRequest request){
        QueryWrapper<User> wrapper  = new QueryWrapper<>();
        wrapper.eq("name",user.getName()).eq("password",user.getPassword());
        User res = userService.getBaseMapper().selectOne(wrapper);
        if (res == null){
            return R.error().message("用户名或密码错误");
        }
        if (res.getIsActive() == 0){
            return R.error().message("账号已冻结");
        }
        Map<String, Object> payload = new HashMap<>();
        payload.put("uid",res.getId());
        System.out.println("===="+payload.get("uid"));
        String token = jwtUtil.encodeToken(payload);
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("token",token);
        return R.ok().detail(jsonObject);
    }
//    注册接口
    @PostMapping("/register")
    public R<?> register(@RequestBody RegisterCodeCheck registerCodeCheck, HttpServletRequest request){
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter("bloom-filter");
        String username = registerCodeCheck.getName();
        String password = registerCodeCheck.getPassword();
        String email = registerCodeCheck.getEmail();
        Long tel = registerCodeCheck.getTel();
        //QueryWrapper<User> wrapper = new QueryWrapper<>();
        //wrapper.eq("name",user.getName());
        //User selectedUser = userService.getBaseMapper().selectOne(wrapper);
        //if (selectedUser != null){
        //    return R.error().message("用户名已存在");
        //}
        //if (username == null ){
        //    return R.error().message("用户名不能为空");
        //}
        //if (username.length()<2 || username.length()>10){
        //    return R.error().message("用户名长度应在2-10个字符之间");
        //}
        //if (password == null ){
        //    return R.error().message("密码不能为空");
        //}
        //if (password.length()<6 || password.length()>15){
        //    return R.error().message("用户名长度应在6-15个字符之间");
        //}
        //if (email == null ){
        //    return R.error().message("邮箱不能为空");
        //}
        //if (tel == null ){
        //    return R.error().message("电话不能为空");
        //}

        CodeCheck codeChecks = new CodeCheck();
        codeChecks.setCode(registerCodeCheck.getCode());
        codeChecks.setMail(registerCodeCheck.getEmail());

        R<?> r = codeCheck(codeChecks, request);
        if (!r.getStatus().equals(ResultCode.SUCCESS)){
            return R.error().message("验证码校验失败");
        }

        User user = new User();
        user.setEmail(registerCodeCheck.getEmail());
        user.setName(registerCodeCheck.getName());
        user.setPassword(registerCodeCheck.getPassword());
        user.setTel(registerCodeCheck.getTel());
        userService.getBaseMapper().insert(user);

        return R.ok().detail(user);
    }
    /**
     * 发送邮箱验证码
     * @param mail 邮箱
     * @param request 请求
     * @return
     */
//    @LogCostTime
    @PostMapping("/mailVerify")
    public R<?> mailVerify(@RequestBody String mail,
                           HttpServletRequest request){
        JSONObject jsonObject = new JSONObject(mail);
        //System.out.println("-------------------"+jsonObject+"-------------------");
        String userMail = jsonObject.getStr("mail");
//        这里应该对空进行校验
//        String cookie = judgeCookieToken.getCookie(request);
        User jwt = (User) request.getAttribute("User");
        emailSendUtil.sendMailVerify(MAIL, userMail,jwt.toString());
//        log.info("邮箱验证码发送成功");
        return R.ok().message("验证码成功发送");
    }
    /**
     * 邮箱验证码校验
     * @param request 请求
     * @return
     */
    @PostMapping("/codeCheck")
    public R<?> codeCheck( @RequestBody CodeCheck codeCheck,
                           HttpServletRequest request){

//        String cookie = judgeCookieToken.getCookie(request);
        User jwt = (User) request.getAttribute("User");
        System.out.println(jwt);
        String md5_R = redisUtil.get(codeCheck.getMail()).toString();
        String md5 = tokenUtil.generateMd5(codeCheck.getMail(), jwt.toString(), codeCheck.getCode());
        if (md5_R==null){

            return R.error().setErrorCode(ResultCode.timeOut);
        }else {
            if (md5.equals(md5_R)){
//                String uid = redisUtil.hmGet(cookie, "uid").toString();
                User u = (User) request.getAttribute("User");
                String key = CacheCode.CACHE_USER + u.getId() ;
                System.out.println(key);
                RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter("bloom-filter");
                if (bloomFilter.contains(key)){
                    User user = (User) redissonClient.getBucket(key).get();
                    user.setEmail(codeCheck.getMail());
                }
                User user = userService.getBaseMapper().selectById(u.getId());
                //  新注册的用户：

                if (user == null) {
                    return R.ok().message("");
                }
                //  老用户修改邮箱
                user.setEmail(codeCheck.getMail());
                int res = userService.getBaseMapper().updateById(user);
                if (res==1){
                    //缓存更新
                    bloomFilter.add(key);
                    redissonClient.getBucket(key).set(user);

                    return R.ok().message("用户邮箱更新成功");
                }else {

                    return R.error().message("用户邮箱更新失败");
                }
            }else {

                return R.error().message("邮箱验证码错误");
            }
        }
    }
    //个人信息修改:用户修改个人信息
    @PutMapping("/{user_id}")
    public R<?> updateUser(@RequestBody UserInfo userInfo, HttpServletRequest request, HttpServletResponse response, @PathVariable Integer user_id){
        User user = (User)request.getAttribute("User");
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("name",userInfo.getName());
        User selectedUser = userService.getBaseMapper().selectOne(wrapper);
        if (selectedUser != null){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return R.error().message("用户名已存在");
        }
        User newUser = new User();
        newUser.setName(userInfo.getName());
        newUser.setPassword(userInfo.getPassword());

        int res = userService.updateUser(newUser,new QueryWrapper<User>().eq("id",user_id));
        if (res == 0){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return R.error().message("更改信息失败");
        }
        return R.ok().message("");
    }


}
