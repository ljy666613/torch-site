package com.example.torchwebsite.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.api.pojo.Comment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
