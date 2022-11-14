package com.example.torchwebsite.config.MQConfig;

import com.example.api.constants.UserConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQUserConfig {
    @Bean
    public TopicExchange topicUserExchange() {
        return new TopicExchange(UserConstants.USER_EXCHANGE, true, false);
    }

    @Bean
    public Queue insertUserQueue() {
        return new Queue(UserConstants.USER_INSERT_QUEUE, true);
    }

    @Bean
    public Queue deleteUserQueue() {
        return new Queue(UserConstants.USER_DELETE_QUEUE, true);
    }

    @Bean
    public Binding insertUserQueueBinding() {
        return BindingBuilder.bind(insertUserQueue()).to(topicUserExchange()).with(UserConstants.USER_INSERT_KEY);
    }

    @Bean
    public Binding deleteUserQueueBinding() {
        return BindingBuilder.bind(deleteUserQueue()).to(topicUserExchange()).with(UserConstants.USER_DELETE_KEY);
    }
}
