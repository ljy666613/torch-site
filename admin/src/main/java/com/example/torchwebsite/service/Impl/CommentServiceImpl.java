package com.example.torchwebsite.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.api.pojo.Comment;
import com.example.torchwebsite.mapper.CommentMapper;
import com.example.torchwebsite.service.CommentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    @Resource
    private CommentMapper commentMapper;
    @Override
    public Page<Comment> getComments(QueryWrapper<Comment> wrapper, Page<Comment> objectPage) {
        return commentMapper.selectPage(objectPage,wrapper);
    }
}
