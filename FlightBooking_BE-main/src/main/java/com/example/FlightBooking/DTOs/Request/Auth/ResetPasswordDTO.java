package com.example.FlightBooking.DTOs.Request.Auth;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResetPasswordDTO {
    private String email;
    private String newPassword;
    private String confirmPassword;
    private Long otp;
}
