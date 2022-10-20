package com.example.front.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.api.pojo.Member;
import org.springframework.stereotype.Service;


public interface MemberService {
    Page<Member> getMember(QueryWrapper<Member> wrapper, Page<Member> objectPage);
    Member getNew(Integer id);
    int insertNew(Member Member);
    int deleteNew(Integer id);
    int updateNew(Member Member,QueryWrapper<Member> wrapper);
}
