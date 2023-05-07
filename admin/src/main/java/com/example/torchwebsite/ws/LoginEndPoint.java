package com.example.torchwebsite.ws;

import cn.hutool.Hutool;
import cn.hutool.core.convert.Convert;
import com.example.api.pojo.Admin;
import com.example.torchwebsite.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

//  websocket连接  这里用于前端给我们发送心跳包~，建立ws连接时，将用户登录状态置为1，关闭ws连接后，将用户登录状态置为0
@ServerEndpoint(value = "/login",configurator = GetHttpSessionConfigurator.class)
@Component
@Slf4j
// WebSocket 每个连接创建一个对象，因此需要用静态资源存储所有对象信息~
public class LoginEndPoint {

//    private static Map<String, String> map = new ConcurrentHashMap<>();

//    静态list存储所有建立了连接的用户的id~
    private static List<String> list = new CopyOnWriteArrayList<>();
//    存储用户id，用于关闭时知道是那个对象关闭了~
    private String id = "";
//    private static CopyOnWriteArraySet<String> set = new CopyOnWriteArraySet<>();

//    private Session session;
//    private HttpSession httpSession;

    private static AdminService adminService;
    @Autowired
    public void setChatService(AdminService adminService) {
        LoginEndPoint.adminService = adminService;
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig config){
        System.out.println("建立连接");
//        this.session = session;
//        HttpSession httpSession = (HttpSession)config.getUserProperties().get(HttpSession.class.getName());
//        this.httpSession = httpSession;
//
//        String name = (String) httpSession.getAttribute("user");
//        onlineUsers.put(name,this);

//        前端传来的格式就是这样
        List<String> strings = (List<String>) config.getUserProperties().get(List.class.getName());
        id =  strings.get(0);

        list.add(id);
        Admin admin = adminService.getAdmin(Convert.toInt(id));

        admin.setIsLogin(1);
        adminService.getBaseMapper().updateById(admin);


    }
    @OnMessage
    public void onMessage(Session session,String message){

    }
    @OnClose
    public void onClose(Session session){
        System.out.println("关闭连接");
        Admin admin = adminService.getBaseMapper().selectById(id);
        admin.setIsLogin(0);
        adminService.getBaseMapper().updateById(admin);
//        由于List源码中有两个remove方法：
//        若在移除时直接写 remove(x)，则x会被认为是下标，而不是集合内的元素。
//        需要在移除int元素时进行封装一下调用Object参数的remove方法即可。remove(new Integer(x))，避免报下标越界问题。
//        String remove = list.remove(1);
//        boolean remove1 = list.remove(new Integer(1));
//        boolean remove2 = list.remove((Integer) 1);
        boolean s = list.remove(id);
        if (s){
            System.out.println("已删除该endpoint！");
        }else {
            System.out.println("list中并不存在该endpoint");
//            throw new NotFoundException("list中不存在");
        }

    }
    @OnError
    public void onError(Session session,Throwable throwable){
        throwable.printStackTrace();
        System.out.println("出错了！");
    }

    /**
     * 实现服务器主动推送
     */
//    public void sendMessage(String message) throws IOException {
//        this.session.getBasicRemote().sendText(message);
//    }

    /**
     * 群发自定义消息 or 发给指定用户
     */
//    public static void sendInfo(String message, @PathParam("name") String name) {
////        log.info("推送消息到窗口" + sid + "，推送内容:" + message);
//        log.info("已调用");
//        for (String item : onlineUsers.keySet()) {
//            try {
//                //这里可以设定只推送给这个sid的，为null则全部推送
//                if (name == null) {
////                    item.sendMessage(message);
//                } else if (item.equals(name)) {
//                    onlineUsers.get(item).sendMessage(message);
//                }
//            } catch (IOException e) {
//            }
//        }
//    }
}
