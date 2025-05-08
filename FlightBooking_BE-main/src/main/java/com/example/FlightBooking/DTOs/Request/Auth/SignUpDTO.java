package com.example.FlightBooking.DTOs.Request.Auth;

import com.example.FlightBooking.Enum.Roles;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Set;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.Getter;
import lombok.Setter;
import springfox.documentation.annotations.ApiIgnore;

@Getter
@Setter

public class SignUpDTO {
    private String username;
    private String password;
    private String email;
    private Roles role;
    private Timestamp dayOfBirth;
    private String fullName;
}
