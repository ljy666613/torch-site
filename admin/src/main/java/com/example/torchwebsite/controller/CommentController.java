package com.example.torchwebsite.controller;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.api.pojo.Comment;
import com.example.api.pojo.vo.Comment.CommentInfo;
import com.example.torchwebsite.service.CommentService;
import com.example.torchwebsite.utils.R;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/api/admin/comment")
public class CommentController {
    @Resource
    private CommentService commentService;
    //    查询评论
    @GetMapping
    public R<?> selectComments(HttpServletRequest request, HttpServletResponse response,
                                 @RequestParam(defaultValue = "1") Integer pageNum,
                                 @RequestParam(defaultValue = "10") Integer pageSize,
                                 @RequestParam(defaultValue = "") String search){

        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.like("name",search);
        Page<Comment> comments = commentService.getComments(wrapper,new Page<>(pageNum, pageSize));
        List<Comment> records = comments.getRecords();
        long total = comments.getTotal();
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("records",records);
        jsonObject.set("total",total);

        return R.ok().detail(jsonObject);
    }

    //删除评论
    @DeleteMapping("/{comment_id}")
    public R<?> deleteComment(@PathVariable Integer comment_id,HttpServletRequest request, HttpServletResponse response){

        int res = commentService.getBaseMapper().deleteById(comment_id);
        if (res == 0){
            return R.error().message("数据库删除失败");
        }
        return R.ok().message("");
    }

    //    更改评论信息
    @PutMapping("/{comment_id}")
    public R<?> updateComment(@RequestBody CommentInfo commentInfo, HttpServletRequest request, HttpServletResponse response, @PathVariable Integer comment_id){

//        数据库可能已有的内容
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
//        wrapper.eq("name",commentInfo.getName());
        wrapper.ne("id",comment_id);
        Comment selectedComment = commentService.getBaseMapper().selectOne(wrapper);
        if (selectedComment != null){
            return R.error().message("评论名已存在");
        }
        Comment newComment = new Comment();
        newComment.setId(comment_id);
//        newComment.setContent(commentInfo.getContent());
//        newComment.setUrl(commentInfo.getUrl());
//        newComment.setImage(commentInfo.getImage());
//        newComment.setPlace(commentInfo.getPlace());
//        newComment.setDate(commentInfo.getDate());
//        newComment.setName(commentInfo.getName());
        int res = commentService.getBaseMapper().update(newComment,new QueryWrapper<Comment>().eq("id",comment_id));
        if (res == 0){
            return R.error().message("更改信息失败");
        }
        return R.ok().message("");
    }

    //添加评论
    @PostMapping
    public R<?> insertComment(@RequestBody CommentInfo commentInfo, HttpServletRequest request, HttpServletResponse response){

        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
//        wrapper.eq("name",commentInfo.getName());
        Comment selectedComment = commentService.getBaseMapper().selectOne(wrapper);
        if (selectedComment != null){
            return R.error().message("评论名已存在");
        }
        Comment newComment = new Comment();
//        newComment.setName(commentInfo.getName());
//        newComment.setContent(commentInfo.getContent());
//        newComment.setUrl(commentInfo.getUrl());
//        newComment.setImage(commentInfo.getImage());
//        newComment.setPlace(commentInfo.getPlace());
//        newComment.setDate(commentInfo.getDate());
//        newComment.setName(commentInfo.getName());



        int res = commentService.getBaseMapper().insert(newComment);
        if (res == 0){

            return R.error().message("数据库添加失败");
        }
        return R.ok().message("");
    }

    @GetMapping("/{comment_id}")
    public R<?> getComment(@PathVariable Integer comment_id){
        Comment comment = commentService.getBaseMapper().selectById(comment_id);
        return R.ok().detail(comment);
    }

//    查看父级评论下的分级评论
    @GetMapping("/mulComment/{parent_id}")
    public R<?> getMulComment(@PathVariable Integer parent_id){
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id",parent_id);
        List<Comment> comments = commentService.getBaseMapper().selectList(wrapper);
        return R.ok().detail(comments);
    }
}
