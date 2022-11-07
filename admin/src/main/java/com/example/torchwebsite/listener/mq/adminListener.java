package com.example.torchwebsite.listener.mq;

import com.example.api.constants.AdminConstants;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class adminListener {
    @RabbitListener(queues = AdminConstants.ADMIN_INSERT_QUEUE)
    public void insertListener(Long id){
        System.out.println(id);
    }

}
