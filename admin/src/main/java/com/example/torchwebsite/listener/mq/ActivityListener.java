package com.example.torchwebsite.listener.mq;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import com.example.api.constants.ActivityConstants;
import com.example.api.pojo.Activity;
import com.example.torchwebsite.service.ActivityService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

@Component
//  rabbitmq 的有关activity的监听器，队列一旦有消息，就消费。
//  这里暂时只有：将数据库更改的信息更新到es索引库中。
public class ActivityListener {
    @Resource
    private ActivityService activityService;

    @Resource
    private ElasticsearchClient client;

    @RabbitListener(queues = ActivityConstants.ACTIVITY_INSERT_QUEUE)
    public void insertListener(Integer id){
        Activity activity = activityService.getBaseMapper().selectById(id);
        IndexResponse response = null;
        try {
            response = client.index(i -> i
                    .index("torchwebsite-activity")
                    .id(activity.getId().toString())
                    .document(activity)
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Indexed with version " + response.version());
    }

    @RabbitListener(queues = ActivityConstants.ACTIVITY_DELETE_QUEUE)
    public void deleteListener(Long id){
        DeleteResponse response = null;
        try {
            response = client.delete(d -> d.index("torchwebsite-activity").id(id.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(response);
    }



}
