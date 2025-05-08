package com.example.FlightBooking.Config.WebSocket.DTO;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class MessageDTO {
    private Long id;
    private String content;
    private Long senderId;
    private Long receiverId;
    private Long sessionId;
    private Timestamp createdAt;
}
