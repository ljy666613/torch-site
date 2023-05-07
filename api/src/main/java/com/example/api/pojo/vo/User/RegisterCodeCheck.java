package com.example.api.pojo.vo.User;

import lombok.Data;

@Data
public class RegisterCodeCheck {
    private String code;
    private String name;
    private String password;
    private String email;
    private Long tel;
}
