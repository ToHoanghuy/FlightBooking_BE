package com.example.FlightBooking.DTOs.Request.Auth;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class ForgotPasswordDTO {
    private String email;
}
