package com.example.torchwebsite.controller;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.api.pojo.Member;
import com.example.api.pojo.vo.Member.MemberInfo;
import com.example.torchwebsite.service.MemberService;
import com.example.torchwebsite.utils.R;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/api/admin/member")
public class MemberController {
    @Resource
    private MemberService memberService;
    //    查询成员
    @GetMapping
    public R<?> selectMembers(HttpServletRequest request, HttpServletResponse response,
                                 @RequestParam(defaultValue = "1") Integer pageNum,
                                 @RequestParam(defaultValue = "10") Integer pageSize,
                                 @RequestParam(defaultValue = "") String search){

        QueryWrapper<Member> wrapper = new QueryWrapper<>();
        wrapper.like("name",search);
        Page<Member> members = memberService.getBaseMapper().selectPage(new Page<>(pageNum, pageSize),wrapper);
        List<Member> records = members.getRecords();
        long total = members.getTotal();
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("records",records);
        jsonObject.set("total",total);

        return R.ok().detail(jsonObject);
    }

    //删除成员
    @DeleteMapping("/{member_id}")
    public R<?> deleteMember(@PathVariable Integer member_id,HttpServletRequest request, HttpServletResponse response){

        int res = memberService.getBaseMapper().deleteById(member_id);
        if (res == 0){
            return R.error().message("数据库删除失败");
        }
        return R.ok().message("");
    }

    //    更改成员信息
    @PutMapping("/{member_id}")
    public R<?> updateMember(@RequestBody MemberInfo memberInfo, HttpServletRequest request, HttpServletResponse response, @PathVariable Integer member_id){

//        数据库可能已有的内容
        QueryWrapper<Member> wrapper = new QueryWrapper<>();
//        wrapper.eq("name",memberInfo.getName());
        wrapper.ne("id",member_id);
        Member selectedMember = memberService.getBaseMapper().selectOne(wrapper);
        if (selectedMember != null){
            return R.error().message("成员名已存在");
        }
        Member newMember = new Member();
        newMember.setId(member_id);
//        newMember.setContent(memberInfo.getContent());
//        newMember.setUrl(memberInfo.getUrl());
//        newMember.setImage(memberInfo.getImage());
//        newMember.setPlace(memberInfo.getPlace());
//        newMember.setDate(memberInfo.getDate());
//        newMember.setName(memberInfo.getName());
        int res = memberService.getBaseMapper().update(newMember,new QueryWrapper<Member>().eq("id",member_id));
        if (res == 0){
            return R.error().message("更改信息失败");
        }
        return R.ok().message("");
    }

    //添加成员
    @PostMapping
    public R<?> insertMember(@RequestBody MemberInfo memberInfo, HttpServletRequest request, HttpServletResponse response){

        QueryWrapper<Member> wrapper = new QueryWrapper<>();
//        wrapper.eq("name",memberInfo.getName());
        Member selectedMember = memberService.getBaseMapper().selectOne(wrapper);
        if (selectedMember != null){
            return R.error().message("成员名已存在");
        }
        Member newMember = new Member();
//        newMember.setName(memberInfo.getName());
//        newMember.setContent(memberInfo.getContent());
//        newMember.setUrl(memberInfo.getUrl());
//        newMember.setImage(memberInfo.getImage());
//        newMember.setPlace(memberInfo.getPlace());
//        newMember.setDate(memberInfo.getDate());
//        newMember.setName(memberInfo.getName());



        int res = memberService.getBaseMapper().insert(newMember);
        if (res == 0){

            return R.error().message("数据库添加失败");
        }
        return R.ok().message("");
    }

    @GetMapping("/{member_id}")
    public R<?> getMember(@PathVariable Integer member_id){
        Member member = memberService.getBaseMapper().selectById(member_id);
        return R.ok().detail(member);
    }
}
