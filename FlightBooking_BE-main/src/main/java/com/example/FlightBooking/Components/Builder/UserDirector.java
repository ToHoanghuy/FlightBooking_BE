package com.example.FlightBooking.Components.Builder;

import com.example.FlightBooking.DTOs.Request.Auth.SignUpDTO;

public class UserDirector {
    private UserBuilder builder;

    public UserDirector(UserBuilder builder) {
        this.builder = builder;
    }

    public void changeBuilder(UserBuilder builder) {
        this.builder = builder;
    }

    public void constructUser(SignUpDTO signUpDTO) {
        builder.reset();
        builder.setUsername(signUpDTO.getUsername());
        builder.setPassword(signUpDTO.getPassword());
        builder.setEmail(signUpDTO.getEmail());
        builder.setRole(signUpDTO.getRole());
        builder.setFullName(signUpDTO.getFullName());
        builder.setDayOfBirth(signUpDTO.getDayOfBirth());
    }
}
