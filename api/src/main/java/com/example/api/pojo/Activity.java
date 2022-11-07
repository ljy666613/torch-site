package com.example.api.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Activity implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String content;
    private Date date;
    private String url;
    private String place;
    private String image;
}
