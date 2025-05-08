package com.example.FlightBooking.Config.WebSocket.DTO;

import lombok.Data;

@Data
public class SupportSessionDTO {
    private Long id;
    private Long customerId;
    private Long adminId;
    private String status;
    private String latestMessage;
    private String customerName;
    private String customerAvatar;
    public String getCustomerAvatar() {
        return customerAvatar != null ? customerAvatar :"https://cdn-media.sforum.vn/storage/app/media/THANHAN/avatar-trang-64.jpg";
    }
}
