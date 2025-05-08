package com.example.FlightBooking.Components.ChainOfResponsibility;

import com.example.FlightBooking.DTOs.Request.Auth.SignInDTO;
import org.springframework.stereotype.Component;

@Component
public class ValidationHandler extends BaseHandler {
    @Override
    public void handle(SignInDTO request) throws Exception {
        if (request.getUsername() == null || request.getPassword() == null) {
            throw new Exception("Invalid login credentials.");
        }
        System.out.println("Validation passed.");
        forward(request);
    }
}
