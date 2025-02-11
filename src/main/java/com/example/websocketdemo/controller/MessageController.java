package com.example.websocketdemo.controller;

import com.example.websocketdemo.websocket.WebSocketServer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @Author: liuyanqun
 * @date: 2024-07-19  10:18
 * @Description: http发送消息
 */
@RestController
public class MessageController {

    @RequestMapping("/demo")
    public boolean demo(@RequestParam("userId") String userId, @RequestParam("message") String mesage) {
        //给前端web推送数据
        WebSocketServer.sendInfo(userId, mesage);
        return true;
    }

    /**
     * 关闭websocket
     */
    @RequestMapping("/close")
    public boolean close(@RequestParam("userId") String userId) {
        try {
            WebSocketServer.close(userId);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }


}
