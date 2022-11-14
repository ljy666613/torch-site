package com.example.torchwebsite.listener.mq;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import com.example.api.constants.UserConstants;
import com.example.api.pojo.User;
import com.example.torchwebsite.service.UserService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

@Component
public class UserListener {
    @Resource
    private UserService userService;

    @Resource
    private ElasticsearchClient client;

    @RabbitListener(queues = UserConstants.USER_INSERT_QUEUE)
    public void insertListener(Integer id){
        User user = userService.getBaseMapper().selectById(id);
        IndexResponse response = null;
        try {
            response = client.index(i -> i
                    .index("torchwebsite-user")
                    .id(user.getId().toString())
                    .document(user)
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Indexed with version " + response.version());
    }

    @RabbitListener(queues = UserConstants.USER_DELETE_QUEUE)
    public void deleteListener(Long id){
        DeleteResponse response = null;
        try {
            response = client.delete(d -> d.index("torchwebsite-user").id(id.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(response);
    }



}
