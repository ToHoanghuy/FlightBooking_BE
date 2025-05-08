package com.example.FlightBooking.Config.WebSocket.DTO;

import lombok.Data;

@Data
public class UserWithLatestMessageDTO {
    private Long id;
    private String fullName;
    private String avatarUrl;

    public UserWithLatestMessageDTO(Long id, String name, String avatar) {
        this.id = id;
        this.fullName = name;
        this.avatarUrl = avatar;
    }
}
