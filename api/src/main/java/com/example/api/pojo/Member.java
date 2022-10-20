package com.example.api.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Member {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String image;
}
