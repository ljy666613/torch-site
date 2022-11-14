package com.example.api.constants;

//  声明rabbitmq中，有关activity的 exchange、queue名称等等~~~
public class ActivityConstants {
    /**
     * 交换机
     */
    public final static String ACTIVITY_EXCHANGE = "activity.topic";
    /**
     * 监听新增和修改的队列
     */
    public final static String ACTIVITY_INSERT_QUEUE = "activity.insert.queue";
    /**
     * 监听删除的队列
     */
    public final static String ACTIVITY_DELETE_QUEUE = "activity.delete.queue";
    /**
     * 新增或修改的RoutingKey
     */
    public final static String ACTIVITY_INSERT_KEY = "activity.insert";
    /**
     * 删除的RoutingKey
     */
    public final static String ACTIVITY_DELETE_KEY = "activity.delete";
}
