package com.example.api.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

@Data
public class Donation implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String amount;
    private String tel;
    private String email;
    private String message;
    private Integer way;
    private Integer isAnonymous;
    private String anonymity;

}
