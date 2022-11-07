package com.example.torchwebsite.config;

import com.example.api.constants.AdminConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQConfig {
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(AdminConstants.ADMIN_EXCHANGE, true, false);
    }

    @Bean
    public Queue insertQueue() {
        return new Queue(AdminConstants.ADMIN_INSERT_QUEUE, true);
    }

    @Bean
    public Queue deleteQueue() {
        return new Queue(AdminConstants.ADMIN_DELETE_QUEUE, true);
    }

    @Bean
    public Binding insertQueueBinding() {
        return BindingBuilder.bind(insertQueue()).to(topicExchange()).with(AdminConstants.ADMIN_INSERT_KEY);
    }

    @Bean
    public Binding deleteQueueBinding() {
        return BindingBuilder.bind(deleteQueue()).to(topicExchange()).with(AdminConstants.ADMIN_DELETE_KEY);
    }
}
