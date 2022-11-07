package com.example.torchwebsite.controller;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.api.pojo.Volunteer;
import com.example.api.pojo.vo.Volunteer.VolunteerInfo;
import com.example.torchwebsite.service.VolunteerService;
import com.example.torchwebsite.utils.R;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/api/admin/volunteer")
public class VolunteerController {
    @Resource
    private VolunteerService volunteerService;
    //    查询志愿者
    @GetMapping
    public R<?> selectVolunteers(HttpServletRequest request, HttpServletResponse response,
                                 @RequestParam(defaultValue = "1") Integer pageNum,
                                 @RequestParam(defaultValue = "10") Integer pageSize,
                                 @RequestParam(defaultValue = "") String search){

        QueryWrapper<Volunteer> wrapper = new QueryWrapper<>();
        wrapper.like("name",search);
        Page<Volunteer> volunteers = volunteerService.getBaseMapper().selectPage(new Page<>(pageNum, pageSize),wrapper);
        List<Volunteer> records = volunteers.getRecords();
        long total = volunteers.getTotal();
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("records",records);
        jsonObject.set("total",total);

        return R.ok().detail(jsonObject);
    }

    //删除志愿者
    @DeleteMapping("/{volunteer_id}")
    public R<?> deleteVolunteer(@PathVariable Integer volunteer_id,HttpServletRequest request, HttpServletResponse response){

        int res = volunteerService.getBaseMapper().deleteById(volunteer_id);
        if (res == 0){
            return R.error().message("数据库删除失败");
        }
        return R.ok().message("");
    }

    //    更改志愿者信息
    @PutMapping("/{volunteer_id}")
    public R<?> updateVolunteer(@RequestBody VolunteerInfo volunteerInfo, HttpServletRequest request, HttpServletResponse response, @PathVariable Integer volunteer_id){

//        数据库可能已有的内容
        QueryWrapper<Volunteer> wrapper = new QueryWrapper<>();
//        wrapper.eq("name",volunteerInfo.getName());
        wrapper.ne("id",volunteer_id);
        Volunteer selectedVolunteer = volunteerService.getBaseMapper().selectOne(wrapper);
        if (selectedVolunteer != null){
            return R.error().message("志愿者名已存在");
        }
        Volunteer newVolunteer = new Volunteer();
        newVolunteer.setId(volunteer_id);
//        newVolunteer.setContent(volunteerInfo.getContent());
//        newVolunteer.setUrl(volunteerInfo.getUrl());
//        newVolunteer.setImage(volunteerInfo.getImage());
//        newVolunteer.setPlace(volunteerInfo.getPlace());
//        newVolunteer.setDate(volunteerInfo.getDate());
//        newVolunteer.setName(volunteerInfo.getName());
        int res = volunteerService.getBaseMapper().update(newVolunteer,new QueryWrapper<Volunteer>().eq("id",volunteer_id));
        if (res == 0){
            return R.error().message("更改信息失败");
        }
        return R.ok().message("");
    }

    //添加志愿者
    @PostMapping
    public R<?> insertVolunteer(@RequestBody VolunteerInfo volunteerInfo, HttpServletRequest request, HttpServletResponse response){

        QueryWrapper<Volunteer> wrapper = new QueryWrapper<>();
//        wrapper.eq("name",volunteerInfo.getName());
        Volunteer selectedVolunteer = volunteerService.getBaseMapper().selectOne(wrapper);
        if (selectedVolunteer != null){
            return R.error().message("志愿者名已存在");
        }
        Volunteer newVolunteer = new Volunteer();
//        newVolunteer.setName(volunteerInfo.getName());
//        newVolunteer.setContent(volunteerInfo.getContent());
//        newVolunteer.setUrl(volunteerInfo.getUrl());
//        newVolunteer.setImage(volunteerInfo.getImage());
//        newVolunteer.setPlace(volunteerInfo.getPlace());
//        newVolunteer.setDate(volunteerInfo.getDate());
//        newVolunteer.setName(volunteerInfo.getName());



        int res = volunteerService.getBaseMapper().insert(newVolunteer);
        if (res == 0){

            return R.error().message("数据库添加失败");
        }
        return R.ok().message("");
    }

    @GetMapping("/{volunteer_id}")
    public R<?> getVolunteer(@PathVariable Integer volunteer_id){
        Volunteer volunteer = volunteerService.getBaseMapper().selectById(volunteer_id);
        return R.ok().detail(volunteer);
    }
}
