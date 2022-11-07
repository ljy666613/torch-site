//package com.example.torchwebsite.ws;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.http.HttpSession;
//import javax.websocket.*;
//import javax.websocket.server.PathParam;
//import javax.websocket.server.ServerEndpoint;
//import java.io.IOException;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.CopyOnWriteArraySet;
//
//@ServerEndpoint(value = "/login",configurator = GetHttpSessionConfigurator.class)
//@Component
//@Slf4j
//public class LoginEndPoint {
//    private static Map<String, LoginEndPoint> onlineUsers = new ConcurrentHashMap<>();
//    private static CopyOnWriteArraySet set = new CopyOnWriteArraySet();
//    private Session session;
//    private HttpSession httpSession;
//
//    @OnOpen
//    public void onOpen(Session session, EndpointConfig config){
//        this.session = session;
//        HttpSession httpSession = (HttpSession)config.getUserProperties().get(HttpSession.class.getName());
//        this.httpSession = httpSession;
//
//        String name = (String) httpSession.getAttribute("user");
//        onlineUsers.put(name,this);
//
//    }
//    @OnMessage
//    public void onMessage(Session session,String message){
//
//    }
//    @OnClose
//    public void onClose(Session session){
//        onlineUsers.remove("......");
//    }
//
//    /**
//     * 实现服务器主动推送
//     */
//    public void sendMessage(String message) throws IOException {
//        this.session.getBasicRemote().sendText(message);
//    }
//
//    /**
//     * 群发自定义消息 or 发给指定用户
//     */
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
//}
