package com.example.front.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.api.pojo.News;


import java.util.List;

public interface NewsService extends IService<News>{
    Page<News> getNews(QueryWrapper<News> wrapper, Page<News> objectPage);
    News getNew(Integer id);
    int insertNew(News news);
    int deleteNew(Integer id);
    int updateNew(News news,QueryWrapper<News> wrapper);
}
