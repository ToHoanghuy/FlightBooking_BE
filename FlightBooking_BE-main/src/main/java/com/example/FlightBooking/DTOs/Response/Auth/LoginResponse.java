package com.example.FlightBooking.DTOs.Response.Auth;

import com.example.FlightBooking.Enum.Roles;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import springfox.documentation.annotations.ApiIgnore;

@Getter
@Setter
@Hidden
public class LoginResponse {
    private String tokenAccess;
    private String tokenRefresh;
    private long expiresRefreshIn;
    private long expiresIn;
    private String username;
    private Roles role;
}
