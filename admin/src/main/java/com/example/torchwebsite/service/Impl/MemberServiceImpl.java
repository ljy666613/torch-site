package com.example.torchwebsite.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.api.pojo.Member;
import com.example.torchwebsite.mapper.MemberMapper;
import com.example.torchwebsite.service.MemberService;
import org.springframework.stereotype.Service;

@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements MemberService {
}