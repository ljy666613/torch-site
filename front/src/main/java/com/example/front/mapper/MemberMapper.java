package com.example.front.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.api.pojo.Member;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper extends BaseMapper<Member> {
}
