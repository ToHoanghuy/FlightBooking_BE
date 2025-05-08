package com.example.FlightBooking.DTOs.Response.User;

import com.example.FlightBooking.Enum.Roles;
import com.example.FlightBooking.Models.Users;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Data
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private Roles role;
    private String avatarUrl;
    private String gender;
    private Timestamp dayOfBirth;
    private String phoneNumber;
    private String personalId;
    private String address;
}
