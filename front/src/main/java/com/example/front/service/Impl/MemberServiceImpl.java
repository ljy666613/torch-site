package com.example.front.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.api.pojo.Member;
import com.example.front.mapper.MemberMapper;
import com.example.front.service.MemberService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements MemberService {
    @Resource
    private MemberMapper memberMapper;

    @Override
    public Page<Member> getMember(QueryWrapper<Member> wrapper, Page<Member>objectPage) {
        return memberMapper.selectPage(objectPage,wrapper);
    }
    @Override
    public Member getNew(Integer id) {
        return memberMapper.selectById(id);
    }

    @Override
    public int insertNew(Member member) {
        return memberMapper.insert(member);
    }

    @Override
    public int deleteNew(Integer id) {
        return memberMapper.deleteById(id);
    }

    @Override
    public int updateNew(Member member,QueryWrapper<Member> wrapper) {
        return memberMapper.update(member,wrapper);
    }
}
