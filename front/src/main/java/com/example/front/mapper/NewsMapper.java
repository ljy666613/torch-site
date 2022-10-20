package com.example.front.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.api.pojo.News;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NewsMapper extends BaseMapper<News> {

}
