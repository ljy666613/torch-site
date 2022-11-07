package com.example.api.constants;

public class AdminConstants {
    /**
     * 交换机
     */
    public final static String ADMIN_EXCHANGE = "admin.topic";
    /**
     * 监听新增和修改的队列
     */
    public final static String ADMIN_INSERT_QUEUE = "admin.insert.queue";
    /**
     * 监听删除的队列
     */
    public final static String ADMIN_DELETE_QUEUE = "admin.delete.queue";
    /**
     * 新增或修改的RoutingKey
     */
    public final static String ADMIN_INSERT_KEY = "admin.insert";
    /**
     * 删除的RoutingKey
     */
    public final static String ADMIN_DELETE_KEY = "admin.delete";
}
