package com.example.torchwebsite.controller;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.api.pojo.User;
import com.example.api.pojo.vo.User.UserInfo;
import com.example.torchwebsite.service.UserService;
import com.example.torchwebsite.utils.R;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Resource
    private UserService userService;

    //    查询用户
    @GetMapping
    public R<?> selectUsers(HttpServletRequest request, HttpServletResponse response,
                            @RequestParam(defaultValue = "1") Integer pageNum,
                            @RequestParam(defaultValue = "10") Integer pageSize,
                            @RequestParam(defaultValue = "") String search){

        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.like("name",search);
        Page<User> users = userService.getUsers(wrapper,new Page<>(pageNum, pageSize));
        List<User> records = users.getRecords();
        long total = users.getTotal();
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("records",records);
        jsonObject.set("total",total);

        return R.ok().detail(jsonObject);
    }

    //删除用户
    @DeleteMapping("/{user_id}")
    public R<?> deleteUser(@PathVariable Integer user_id,HttpServletRequest request, HttpServletResponse response){

        int res = userService.deleteUser(user_id);
        if (res == 0){
            return R.error().message("数据库删除失败");
        }
        return R.ok().message("");
    }
    //    更改用户信息
    @PutMapping("/{user_id}")
    public R<?> updateUser(@RequestBody UserInfo userInfo, HttpServletRequest request, HttpServletResponse response, @PathVariable Integer user_id){
////        用户自己的信息
//        QueryWrapper<User> wrapperId = new QueryWrapper<>();
//        wrapperId.eq("id", user_id);
//        User user = userService.getBaseMapper().selectOne(wrapperId);

//        数据库可能已有的内容
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("name",userInfo.getName());
        wrapper.ne("id",user_id);
        User selectedUser = userService.getBaseMapper().selectOne(wrapper);
        if (selectedUser != null){
            return R.error().message("用户名已存在");
        }

        QueryWrapper<User> wrapperEmail = new QueryWrapper<>();
        wrapperEmail.eq("email",userInfo.getEmail());
        User selectedUserEmail = userService.getBaseMapper().selectOne(wrapperEmail);
        if (selectedUserEmail != null && selectedUserEmail.getId()!=user_id){
            return R.error().message("邮箱已注册");
        }

        QueryWrapper<User> wrapperTel = new QueryWrapper<>();
        wrapperTel.eq("tel",userInfo.getTel());
        User selectedUserTel = userService.getBaseMapper().selectOne(wrapperTel);
        if (selectedUserTel != null && selectedUserTel.getId()!=user_id){
            return R.error().message("手机号已注册");
        }


        User newUser = new User();
        newUser.setId(user_id);
        newUser.setName(userInfo.getName());
        newUser.setPassword(userInfo.getPassword());
        newUser.setEmail(userInfo.getEmail());
        newUser.setTel(userInfo.getTel());
        int res = userService.updateUser(newUser,new QueryWrapper<User>().eq("id",user_id));
        if (res == 0){
            return R.error().message("更改信息失败");
        }
        return R.ok().message("");
    }

    //添加水军
    @PostMapping
    public R<?> insertUser(@RequestBody UserInfo userInfo, HttpServletRequest request, HttpServletResponse response){

        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("name",userInfo.getName());
        User selectedUser = userService.getBaseMapper().selectOne(wrapper);
        if (selectedUser != null){
            return R.error().message("用户名已存在");
        }

        QueryWrapper<User> wrapperEmail = new QueryWrapper<>();
        wrapperEmail.eq("email",userInfo.getEmail());
        User selectedUserEmail = userService.getBaseMapper().selectOne(wrapperEmail);
        if (selectedUserEmail != null){
            return R.error().message("邮箱已注册");
        }

        QueryWrapper<User> wrapperTel = new QueryWrapper<>();
        wrapperTel.eq("tel",userInfo.getTel());
        User selectedUserTel = userService.getBaseMapper().selectOne(wrapperTel);
        if (selectedUserTel != null){

            return R.error().message("手机号已注册");
        }


        User newUser = new User();
        newUser.setName(userInfo.getName());
        newUser.setPassword(userInfo.getPassword());
        newUser.setEmail(userInfo.getEmail());
        newUser.setTel(userInfo.getTel());



        int res = userService.insertUser(newUser);
        if (res == 0){

            return R.error().message("数据库添加失败");
        }
        return R.ok().message("");
    }

    @GetMapping("/{user_id}")
    public R<?> getUser(@PathVariable Integer user_id){
        User user = userService.getUser(user_id);
        return R.ok().detail(user);
    }
//    管理员管理用户状态
    @PutMapping("/status/{user_id}")
    public R<?> updateStatus(HttpServletRequest request, HttpServletResponse response, @PathVariable Integer user_id){
        User selectedUser = userService.getBaseMapper().selectOne(new QueryWrapper<User>().eq("id", user_id));
        if (selectedUser == null){
           return R.error().message("该用户不存在");
        }
        if (selectedUser.getIsActive() == 1){
            selectedUser.setIsActive(0);
        }else if (selectedUser.getIsActive() == 0){
           selectedUser.setIsActive(1);
        }
        int res = userService.getBaseMapper().updateById(selectedUser);
    
        if (res == 0){
            return R.error().message("更改信息失败");
        }

        return R.ok().message("");

    }
    @PutMapping("/exit/{user_id}")
    public R<?> updateIsLogin(@PathVariable Integer user_id){
        User user = userService.getBaseMapper().selectOne(new QueryWrapper<User>().eq("id", user_id));
        if (user.getIsLogin() == 0){
            user.setIsLogin(1);
        }else if(user.getIsLogin() == 1){
            user.setIsLogin(0);
        }
        userService.getBaseMapper().updateById(user);
        return R.ok().message("");
    }
}
