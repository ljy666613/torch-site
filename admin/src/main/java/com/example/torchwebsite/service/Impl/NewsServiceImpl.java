package com.example.torchwebsite.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.api.pojo.News;
import com.example.torchwebsite.mapper.NewsMapper;
import com.example.torchwebsite.service.NewsService;
import org.springframework.stereotype.Service;

@Service
public class NewsServiceImpl extends ServiceImpl<NewsMapper, News> implements NewsService {

}
