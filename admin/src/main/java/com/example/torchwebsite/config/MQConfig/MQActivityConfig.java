package com.example.torchwebsite.config.MQConfig;

import com.example.api.constants.ActivityConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//Activity的exchange 以及 queue 的 配置 其他的同理~
public class MQActivityConfig {
    @Bean
    public TopicExchange topicActivityExchange() {
        return new TopicExchange(ActivityConstants.ACTIVITY_EXCHANGE, true, false);
    }

    @Bean
    public Queue insertActivityQueue() {
        return new Queue(ActivityConstants.ACTIVITY_INSERT_QUEUE, true);
    }

    @Bean
    public Queue deleteActivityQueue() {
        return new Queue(ActivityConstants.ACTIVITY_DELETE_QUEUE, true);
    }

    @Bean
    public Binding insertActivityQueueBinding() {
        return BindingBuilder.bind(insertActivityQueue()).to(topicActivityExchange()).with(ActivityConstants.ACTIVITY_INSERT_KEY);
    }

    @Bean
    public Binding deleteActivityQueueBinding() {
        return BindingBuilder.bind(deleteActivityQueue()).to(topicActivityExchange()).with(ActivityConstants.ACTIVITY_DELETE_KEY);
    }
}
