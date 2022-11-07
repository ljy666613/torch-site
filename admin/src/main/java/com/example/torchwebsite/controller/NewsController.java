package com.example.torchwebsite.controller;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.api.pojo.News;
import com.example.api.pojo.vo.News.NewsInfo;
import com.example.torchwebsite.service.NewsService;
import com.example.torchwebsite.utils.R;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/api/admin/news")
public class NewsController {
    @Resource
    private NewsService newsService;
    //    查询要闻
    @GetMapping
    public R<?> selectNews(HttpServletRequest request, HttpServletResponse response,
                                 @RequestParam(defaultValue = "1") Integer pageNum,
                                 @RequestParam(defaultValue = "10") Integer pageSize,
                                 @RequestParam(defaultValue = "") String search){

        QueryWrapper<News> wrapper = new QueryWrapper<>();
        wrapper.like("name",search);
        Page<News> news = newsService.getBaseMapper().selectPage(new Page<>(pageNum, pageSize), wrapper);
        List<News> records = news.getRecords();
        long total = news.getTotal();
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("records",records);
        jsonObject.set("total",total);

        return R.ok().detail(jsonObject);
    }

    //删除要闻
    @DeleteMapping("/{news_id}")
    public R<?> deleteNews(@PathVariable Integer news_id,HttpServletRequest request, HttpServletResponse response){

        int res = newsService.getBaseMapper().deleteById(news_id);
        if (res == 0){
            return R.error().message("数据库删除失败");
        }
        return R.ok().message("");
    }

    //    更改要闻信息
    @PutMapping("/{news_id}")
    public R<?> updateNews(@RequestBody NewsInfo newsInfo, HttpServletRequest request, HttpServletResponse response, @PathVariable Integer news_id){

//        数据库可能已有的内容
        QueryWrapper<News> wrapper = new QueryWrapper<>();
//        wrapper.eq("name",newsInfo.getName());
        wrapper.ne("id",news_id);
        News selectedNews = newsService.getBaseMapper().selectOne(wrapper);
        if (selectedNews != null){
            return R.error().message("要闻名已存在");
        }
        News newNews = new News();
        newNews.setId(news_id);
//        newNews.setContent(newsInfo.getContent());
//        newNews.setUrl(newsInfo.getUrl());
//        newNews.setImage(newsInfo.getImage());
//        newNews.setPlace(newsInfo.getPlace());
//        newNews.setDate(newsInfo.getDate());
//        newNews.setName(newsInfo.getName());
        int res = newsService.getBaseMapper().update(newNews,new QueryWrapper<News>().eq("id",news_id));
        if (res == 0){
            return R.error().message("更改信息失败");
        }
        return R.ok().message("");
    }

    //添加要闻
    @PostMapping
    public R<?> insertNews(@RequestBody NewsInfo newsInfo, HttpServletRequest request, HttpServletResponse response){

        QueryWrapper<News> wrapper = new QueryWrapper<>();
//        wrapper.eq("name",newsInfo.getName());
        News selectedNews = newsService.getBaseMapper().selectOne(wrapper);
        if (selectedNews != null){
            return R.error().message("要闻名已存在");
        }
        News newNews = new News();
//        newNews.setName(newsInfo.getName());
//        newNews.setContent(newsInfo.getContent());
//        newNews.setUrl(newsInfo.getUrl());
//        newNews.setImage(newsInfo.getImage());
//        newNews.setPlace(newsInfo.getPlace());
//        newNews.setDate(newsInfo.getDate());
//        newNews.setName(newsInfo.getName());



        int res = newsService.getBaseMapper().insert(newNews);
        if (res == 0){

            return R.error().message("数据库添加失败");
        }
        return R.ok().message("");
    }

    @GetMapping("/{news_id}")
    public R<?> getNews(@PathVariable Integer news_id){
        News news = newsService.getBaseMapper().selectById(news_id);
        return R.ok().detail(news);
    }
}
