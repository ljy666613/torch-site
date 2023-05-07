package com.example.api.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

@Data
public class Comment implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer activity_id;
    private String content;
    private Integer parent_id;
    private Integer user_id;
    private Integer reply_id;

}
