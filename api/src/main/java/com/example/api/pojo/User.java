package com.example.api.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class User {
    @TableId(type = IdType.AUTO)
    private Integer id;
    @Length(min = 1, max = 12)
    private String name;
    @Length(min = 1, max = 20)
    private String password;
    private String email;
    private Long tel;
    private Integer isActive;
}
