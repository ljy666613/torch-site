package com.example.front.controller;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.api.pojo.News;
import com.example.api.pojo.vo.User.UserInfo;
import com.example.front.service.NewsService;
import com.example.front.service.UserService;
import com.example.front.utils.JwtUtil;
import com.example.front.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.api.pojo.User;
//import com.example.commen.utils.R;
//import com.example.commen.utils.JwtUtil;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/front")
public class NewsController {
    @Resource
    private NewsService newsService;
    private final JwtUtil jwtUtil ;
    @Autowired()
    public NewsController(JwtUtil jwtUtil){
        this.jwtUtil = jwtUtil;
    }
    //薪火要闻获取信息
    @GetMapping("/news")
    public R<?> selectAllNews(HttpServletRequest request, HttpServletResponse response,
                              @RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search){

        QueryWrapper<News> wrapper = new QueryWrapper<>();
        wrapper.like("id",search);
        Page<News> users = newsService.getNews(wrapper,new Page<News>(pageNum, pageSize));
        List<News> records = users.getRecords();

        return R.ok().detail(records);
    }
}
