package com.example.api.pojo.vo.Activity;

import lombok.Data;

import java.util.Date;

@Data
public class ActivityInfo {
    private String name;
    private String content;
    private Date date;
    private String url;
    private String place;
    private String image;
}
