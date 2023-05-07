package com.example.torchwebsite.service;
 
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
 
@Service
public class AsyncService {
 
    @Async("async")
    public void asyncTest(Integer counter) throws InterruptedException{

        System.out.println("线程" + Thread.currentThread().getName() + " 执行异步任务：" + counter);

    }
 
}