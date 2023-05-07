package com.example.torchwebsite.controller;
 
import cn.hutool.json.JSONObject;
import com.example.torchwebsite.service.AsyncService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/async")
@CrossOrigin(value = "*",  maxAge = 3600)
public class AsyncController {
 
    @Resource
    private AsyncService server;
 
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public JSONObject asyncTest() throws InterruptedException{
        JSONObject output = new JSONObject();
        long startTime = System.currentTimeMillis();
        int counter = 10;
        for (int i = 0; i<counter ; i++) {
            server.asyncTest(i);
        }
        long endTime = System.currentTimeMillis();
        output.set("msg", "succeed");
        output.set("花费时间: ", endTime-startTime);
        return output;
    }


 
}