package org.example.backend_fivegivechill.services;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Gửi message tới user cụ thể dựa vào userId
     */
    public void sendForceLogout(int userId, String message) {
        // gửi đến topic riêng của user
        messagingTemplate.convertAndSend("/topic/force-logout/" + userId, message);
    }
}
