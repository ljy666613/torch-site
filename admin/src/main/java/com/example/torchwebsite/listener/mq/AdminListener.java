package com.example.torchwebsite.listener.mq;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import com.example.api.constants.AdminConstants;
import com.example.api.pojo.Admin;
import com.example.torchwebsite.service.AdminService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

@Component
public class AdminListener {
    @Resource
    private AdminService adminService;

    @Resource
    private ElasticsearchClient client;

    @RabbitListener(queues = AdminConstants.ADMIN_INSERT_QUEUE)
    public void insertListener(Integer id){
        Admin admin = adminService.getBaseMapper().selectById(id);
        IndexResponse response = null;
        try {
            response = client.index(i -> i
                    .index("torchwebsite-admin")
                    .id(admin.getId().toString())
                    .document(admin)
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Indexed with version " + response.version());
    }

    @RabbitListener(queues = AdminConstants.ADMIN_DELETE_QUEUE)
    public void deleteListener(Long id){
        DeleteResponse response = null;
        try {
            response = client.delete(d -> d.index("torchwebsite-admin").id(id.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(response);
    }



}
