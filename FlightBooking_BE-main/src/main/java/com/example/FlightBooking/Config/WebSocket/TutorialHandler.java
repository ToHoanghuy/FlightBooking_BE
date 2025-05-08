package com.example.FlightBooking.Config.WebSocket;

import com.example.FlightBooking.Models.Message;
import com.example.FlightBooking.Repositories.MessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class TutorialHandler implements WebSocketHandler {

    private final Set<WebSocketSession> sessions = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @Autowired
    private MessageRepository messageRepository;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        log.info("Connection established on session: {}", session.getId());
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String payload = (String) message.getPayload();
        log.info("Message received: {}", payload);

        ObjectMapper objectMapper = new ObjectMapper();
        Message receivedMessage = objectMapper.readValue(payload, Message.class);

        if (receivedMessage.getSenderId() == null || receivedMessage.getReceiverId() == null || receivedMessage.getMessage() == null || receivedMessage.getMessage().isEmpty()) {
            throw new RuntimeException("Message content cannot be null or empty");
        }

        receivedMessage.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        messageRepository.save(receivedMessage);

        log.info("Sending message: {}", receivedMessage); // Debug log

        for (WebSocketSession sess : sessions) {
            if (sess.isOpen()) {
                sess.sendMessage(new TextMessage(payload));
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.info("Exception occurred: {} on session: {}", exception.getMessage(), session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        sessions.remove(session);
        log.info("Connection closed on session: {} with status: {}", session.getId(), closeStatus.getCode());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
