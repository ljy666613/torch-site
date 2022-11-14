package com.example.torchwebsite.config.MQConfig;

import com.example.api.constants.AdminConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQAdminConfig {
    @Bean
    public TopicExchange topicAdminExchange() {
        return new TopicExchange(AdminConstants.ADMIN_EXCHANGE, true, false);
    }

    @Bean
    public Queue insertAdminQueue() {
        return new Queue(AdminConstants.ADMIN_INSERT_QUEUE, true);
    }

    @Bean
    public Queue deleteAdminQueue() {
        return new Queue(AdminConstants.ADMIN_DELETE_QUEUE, true);
    }

    @Bean
    public Binding insertAdminQueueBinding() {
        return BindingBuilder.bind(insertAdminQueue()).to(topicAdminExchange()).with(AdminConstants.ADMIN_INSERT_KEY);
    }

    @Bean
    public Binding deleteAdminQueueBinding() {
        return BindingBuilder.bind(deleteAdminQueue()).to(topicAdminExchange()).with(AdminConstants.ADMIN_DELETE_KEY);
    }


}
