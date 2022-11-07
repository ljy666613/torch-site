package com.example.torchwebsite.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.api.pojo.Comment;

public interface CommentService extends IService<Comment> {
    Page<Comment> getComments(QueryWrapper<Comment> wrapper, Page<Comment> objectPage);
}
