package com.example.torchwebsite.controller;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.api.constants.ActivityConstants;
import com.example.api.pojo.Activity;
import com.example.api.pojo.vo.Activity.ActivityInfo;
import com.example.torchwebsite.service.ActivityService;
import com.example.torchwebsite.utils.R;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/api/admin/activity")
public class ActivityController {
    @Resource
    private ActivityService activityService;
    @Resource
    private RabbitTemplate rabbitTemplate;
    //    查询活动
    @GetMapping
    public R<?> selectActivities(HttpServletRequest request, HttpServletResponse response,
                            @RequestParam(defaultValue = "1") Integer pageNum,
                            @RequestParam(defaultValue = "10") Integer pageSize,
                            @RequestParam(defaultValue = "") String search){

        QueryWrapper<Activity> wrapper = new QueryWrapper<>();
        wrapper.like("name",search);
        Page<Activity> activities = activityService.getActivities(wrapper,new Page<>(pageNum, pageSize));
        List<Activity> records = activities.getRecords();
        long total = activities.getTotal();
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("records",records);
        jsonObject.set("total",total);

        return R.ok().detail(jsonObject);
    }

    //删除活动
    @DeleteMapping("/{activity_id}")
    public R<?> deleteActivity(@PathVariable Integer activity_id,HttpServletRequest request, HttpServletResponse response){

        int res = activityService.getBaseMapper().deleteById(activity_id);
        if (res == 0){
            return R.error().message("数据库删除失败");
        }
        rabbitTemplate.convertAndSend(ActivityConstants.ACTIVITY_EXCHANGE,ActivityConstants.ACTIVITY_DELETE_KEY,activity_id);
        return R.ok().message("");
    }

    //    更改活动信息
    @PutMapping("/{activity_id}")
    public R<?> updateActivity(@RequestBody ActivityInfo activityInfo, HttpServletRequest request, HttpServletResponse response, @PathVariable Integer activity_id){

//        数据库可能已有的内容
        QueryWrapper<Activity> wrapper = new QueryWrapper<>();
        wrapper.eq("name",activityInfo.getName());
        wrapper.ne("id",activity_id);
        Activity selectedActivity = activityService.getBaseMapper().selectOne(wrapper);
        if (selectedActivity != null){
            return R.error().message("活动名已存在");
        }
        Activity newActivity = new Activity();
        newActivity.setId(activity_id);
        newActivity.setContent(activityInfo.getContent());
        newActivity.setUrl(activityInfo.getUrl());
        newActivity.setImage(activityInfo.getImage());
        newActivity.setPlace(activityInfo.getPlace());
        newActivity.setDate(activityInfo.getDate());
        newActivity.setName(activityInfo.getName());
        int res = activityService.getBaseMapper().update(newActivity,new QueryWrapper<Activity>().eq("id",activity_id));
        if (res == 0){
            return R.error().message("更改信息失败");
        }
        rabbitTemplate.convertAndSend(ActivityConstants.ACTIVITY_EXCHANGE,ActivityConstants.ACTIVITY_INSERT_KEY,newActivity.getId());

        return R.ok().message("");
    }

    //添加活动
    @PostMapping
    public R<?> insertActivity(@RequestBody ActivityInfo activityInfo, HttpServletRequest request, HttpServletResponse response){

        QueryWrapper<Activity> wrapper = new QueryWrapper<>();
        wrapper.eq("name",activityInfo.getName());
        Activity selectedActivity = activityService.getBaseMapper().selectOne(wrapper);
        if (selectedActivity != null){
            return R.error().message("活动名已存在");
        }
        Activity newActivity = new Activity();
        newActivity.setName(activityInfo.getName());
        newActivity.setContent(activityInfo.getContent());
        newActivity.setUrl(activityInfo.getUrl());
        newActivity.setImage(activityInfo.getImage());
        newActivity.setPlace(activityInfo.getPlace());
        newActivity.setDate(activityInfo.getDate());
        newActivity.setName(activityInfo.getName());
        
        int res = activityService.getBaseMapper().insert(newActivity);
        if (res == 0){
            return R.error().message("数据库添加失败");
        }
        rabbitTemplate.convertAndSend(ActivityConstants.ACTIVITY_EXCHANGE,ActivityConstants.ACTIVITY_INSERT_KEY,newActivity.getId());

        return R.ok().message("");
    }

    @GetMapping("/{activity_id}")
    public R<?> getActivity(@PathVariable Integer activity_id){
        Activity activity = activityService.getBaseMapper().selectById(activity_id);
        return R.ok().detail(activity);
    }
}
