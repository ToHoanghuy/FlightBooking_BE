package com.example.FlightBooking.Components.ChainOfResponsibility;

import com.example.FlightBooking.DTOs.Request.Auth.SignInDTO;
import com.example.FlightBooking.DTOs.Response.Auth.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {
    private final ValidationHandler validationHandler;
    private final AuthenticationHandler authenticationHandler;
    private final JwtTokenHandler jwtTokenHandler;

    @Autowired
    public LoginService(ValidationHandler validationHandler, AuthenticationHandler authenticationHandler, JwtTokenHandler jwtTokenHandler) {
        this.validationHandler = validationHandler;
        this.authenticationHandler = authenticationHandler;
        this.jwtTokenHandler = jwtTokenHandler;
        validationHandler.setNext(authenticationHandler);
        authenticationHandler.setNext(jwtTokenHandler);
    }

    public LoginResponse login(SignInDTO request) throws Exception {
        validationHandler.handle(request);
        return jwtTokenHandler.getLoginResponse();
    }
}
