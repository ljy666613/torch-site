package com.example.torchwebsite.ws;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import java.util.List;
import java.util.Map;

//  Websocket 的 配置类
public class GetHttpSessionConfigurator extends ServerEndpointConfig.Configurator {
    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
//        HttpSession httpSession = (HttpSession) request.getHttpSession();
        List<String> pro = request.getHeaders().get("sec-websocket-protocol");
        for (String s : pro) {
            System.out.println(s);
        }
        sec.getUserProperties().put(List.class.getName(),pro);
//        sec.getUserProperties().put(HttpSession.class.getName(),httpSession);
        Map<String, List<String>> headers = response.getHeaders();
        headers.put("sec-websocket-protocol",pro);
    }
}
