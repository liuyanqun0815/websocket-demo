package com.example.websocketdemo.websocket;

import org.springframework.stereotype.Component;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;


/**
 *  @ServerEndpoint:该注解用于暴漏外部ws的路径，类似@RequestMapping注解。例如服务端口8080，请求地址：ws://localhost:8080/myWs
 *  路径上{userId}  可在onOpen连接成功方法使用@PathParam("userId") String userId接收数据
 * @Author: liuyanqun
 * @date: 2024-07-19  10:16
 * @Description: TODO
 */
@ServerEndpoint("/myWs/{userId}")
@Component
public class WebSocketServer {
    //线程安全的map，用来保存每个客户端对应的WebSocket对象
    private static ConcurrentHashMap<String, WebSocketServer> webSocketMap = new ConcurrentHashMap<>();
    //单个客户端的session，通过session与对应客户端通讯
    private Session session;
    //用户id
    private String userId;

    /**
     * 连接成功
     * @OnOpen注解：websocket 连接成功后，触发该注解修饰的方法
     * @param session
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        this.session = session;
        this.userId = userId;
        if (webSocketMap.containsKey(userId)) {
            webSocketMap.remove(userId);
            webSocketMap.put(userId, this);
        } else {
            webSocketMap.put(userId, this);
        }
        System.out.println("连接成功");
    }

    /**
     * 连接关闭
     * @OnClose注解：websocket断开连接后，触发该注解修饰的方法
     * @param session
     */
    @OnClose
    public void onCLose(Session session) {
        if (webSocketMap.containsKey(userId)) {
            webSocketMap.remove(userId);
        }
        System.out.println("关闭连接");
    }

    /**
     * 接收消息
     * @OnMessage注解：客户端发送消息时，触发该注解声明的方法
     * @param text
     * @return
     */
    @OnMessage
    public void onMessage(String text) {
        System.out.println("后端接收前端web发送数据userId:" + userId + ",接收信息：" + text);
        if (webSocketMap.containsKey(userId)) {
            try {
                webSocketMap.get(userId).session.getBasicRemote().sendText("返回web数据userId:" + userId + ",返回消息：" + text);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 连接异常
     * @OnError注解：当建立的连接出现异常后，触发该注解修饰的方法
     * @param session
     * @param throwable
     */
    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("websocket连接异常：" + throwable.getMessage());
    }

    /**
     * 服务器给指定WebSocket客户端发送信息
     * @param userId
     * @param message
     */
    public static void sendInfo(String userId, String message) {
        System.out.println("后端发送前端web数据userId:" + userId + "发送消息：" + message);
        if (webSocketMap.containsKey(userId)) {
            try {
                webSocketMap.get(userId).session.getBasicRemote().sendText("后端发送前端web数据userId" + userId + "，内容：" + message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
