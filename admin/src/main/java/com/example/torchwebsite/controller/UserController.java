package com.example.torchwebsite.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
    public R<?> selectUsers(HttpServletRequest request, HttpServletResponse response){

        QueryWrapper<User> wrapper = new QueryWrapper<>();
        List<User> users = userService.getUsers(wrapper);
        return R.ok().detail(users);
    }

    //删除用户
    @DeleteMapping("/{user_id}")
    public R<?> deleteUser(@PathVariable Integer user_id,HttpServletRequest request, HttpServletResponse response){

        int res = userService.deleteUser(user_id);
        if (res == 0){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return R.error().message("数据库删除失败");
        }
        return R.ok().message("");
    }
    //    更改用户信息
    @PutMapping("/{user_id}")
    public R<?> updateUser(@RequestBody UserInfo userInfo, HttpServletRequest request, HttpServletResponse response, @PathVariable Integer user_id){

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
        newUser.setEmail(userInfo.getEmail());
        newUser.setTel(userInfo.getTel());
        int res = userService.updateUser(newUser,new QueryWrapper<User>().eq("id",user_id));
        if (res == 0){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
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
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return R.error().message("用户名已存在");
        }

        User newUser = new User();
        newUser.setName(userInfo.getName());
        newUser.setPassword(userInfo.getPassword());
        newUser.setEmail(userInfo.getEmail());
        newUser.setTel(userInfo.getTel());

        int res = userService.insertUser(newUser);
        if (res == 0){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return R.error().message("数据库添加失败");
        }
        return R.ok().message("");
    }

    @GetMapping("/{user_id}")
    public R<?> getUser(@PathVariable Integer user_id){
        User user = userService.getUser(user_id);
        return R.ok().detail(user);
    }
}
