package com.example.api.pojo.vo.User;

import lombok.Data;

import java.io.Serializable;

@Data
public class CodeCheck implements Serializable {
     private String mail;
     private String code;
}
