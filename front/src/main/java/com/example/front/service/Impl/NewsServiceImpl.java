package com.example.front.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.api.pojo.News;
import com.example.front.mapper.NewsMapper;
import com.example.front.service.NewsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class NewsServiceImpl extends ServiceImpl<NewsMapper, News> implements NewsService {
    @Resource
    private NewsMapper newsMapper;

    @Override
    public Page<News> getNews(QueryWrapper<News> wrapper, Page<News>objectPage) {
        return newsMapper.selectPage(objectPage,wrapper);
    }
    @Override
    public News getNew(Integer id) {
        return newsMapper.selectById(id);
    }

    @Override
    public int insertNew(News news) {
        return newsMapper.insert(news);
    }

    @Override
    public int deleteNew(Integer id) {
        return newsMapper.deleteById(id);
    }

    @Override
    public int updateNew(News news,QueryWrapper<News> wrapper) {
        return newsMapper.update(news,wrapper);
    }
}
