package com.example.torchwebsite.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.api.pojo.NewsImages;
import com.example.torchwebsite.mapper.NewsImagesMapper;
import com.example.torchwebsite.service.NewsImagesService;
import org.springframework.stereotype.Service;

@Service
public class NewsImagesServiceImpl extends ServiceImpl<NewsImagesMapper, NewsImages> implements NewsImagesService {

}
