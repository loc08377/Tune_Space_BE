package org.example.backend_fivegivechill.Config;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class MusicSocketHandler extends TextWebSocketHandler {
    // giao diện chưa cần tương tác với server bằng websocket này nên ở đây chưa sử dụng nhé
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("Client connected: " + session.getId());
        session.sendMessage(new TextMessage("Kết nối thành công tới server Spring Boot!"));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String clientMessage = message.getPayload();
        System.out.println("📨 Nhận từ client: " + clientMessage);
        session.sendMessage(new TextMessage("Server nhận: " + clientMessage));
    }
}
