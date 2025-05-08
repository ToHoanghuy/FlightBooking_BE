package com.example.FlightBooking.DTOs.Request.Auth;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.Getter;
import lombok.Setter;
import springfox.documentation.annotations.ApiIgnore;
@Getter
@Setter
public class SignInDTO {
    private String username;
    private String password;
}
