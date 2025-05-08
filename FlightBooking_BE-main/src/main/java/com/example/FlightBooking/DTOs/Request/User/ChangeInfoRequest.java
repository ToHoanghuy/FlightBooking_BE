package com.example.FlightBooking.DTOs.Request.User;

import java.sql.Timestamp;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeInfoRequest {
    private String token;
    private String fullName;
    private Timestamp dayOfBirth;
    private String gender;
    private String address;
    private String phoneNumber;
    private String personalId;
}

