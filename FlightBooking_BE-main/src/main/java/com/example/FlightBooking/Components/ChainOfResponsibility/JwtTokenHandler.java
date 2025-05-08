package com.example.FlightBooking.Components.ChainOfResponsibility;

import com.example.FlightBooking.DTOs.Request.Auth.SignInDTO;
import com.example.FlightBooking.DTOs.Response.Auth.LoginResponse;
import com.example.FlightBooking.Models.Users;
import com.example.FlightBooking.Repositories.UserRepository;
import com.example.FlightBooking.Services.AuthJWT.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenHandler extends BaseHandler {
    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    private LoginResponse loginResponse;

    @Override
    public void handle(SignInDTO request) throws Exception {
        Users user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new Exception("User not found."));

        String jwtToken = jwtService.generateToken(user);

        // Create LoginResponse object
        loginResponse = new LoginResponse();
        loginResponse.setTokenAccess(jwtToken);
        // Assuming you have methods to get refresh token and expiration times
        loginResponse.setUsername(user.getUsername());
        loginResponse.setRole(user.getRole());

        System.out.println("JWT Token generated: " + jwtToken);
        forward(request);
    }
    public LoginResponse getLoginResponse() {
        return loginResponse;
    }
}
