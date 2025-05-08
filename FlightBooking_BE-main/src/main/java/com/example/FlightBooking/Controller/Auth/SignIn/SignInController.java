package com.example.FlightBooking.Controller.Auth.SignIn;

import com.example.FlightBooking.Components.ChainOfResponsibility.LoginService;
import com.example.FlightBooking.DTOs.Request.Auth.SignInDTO;
import com.example.FlightBooking.DTOs.Response.Auth.LoginResponse;
import com.example.FlightBooking.Models.Tokens;
import com.example.FlightBooking.Models.Users;
import com.example.FlightBooking.Repositories.TokenRepository;
import com.example.FlightBooking.Services.AuthJWT.AuthenticationService;
import com.example.FlightBooking.Services.AuthJWT.JwtRefreshService;
import com.example.FlightBooking.Services.AuthJWT.JwtService;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@Tag(name = "Authentication", description = "APIs for authenticate for user")
public class SignInController {

    @Autowired
    private LoginService loginService;

    private final JwtService jwtService;
    private final JwtRefreshService jwtRefreshService;
    private final AuthenticationService authenticationService;
    private final TokenRepository tokenRepository;
    public SignInController(JwtService jwtService, JwtRefreshService jwtRefreshService, AuthenticationService authenticationService, TokenRepository tokenRepository) {
        this.jwtService = jwtService;
        this.jwtRefreshService = jwtRefreshService;
        this.authenticationService = authenticationService;
        this.tokenRepository = tokenRepository;
    }
    @PostMapping("/auth/signin")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody SignInDTO loginUserDto) {
        Users authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtTokenAccess = jwtService.generateToken(authenticatedUser);
        String jwtTokenRefresh = jwtRefreshService.generateToken(authenticatedUser);
        Tokens tokens = new Tokens();
        tokens.setUser(authenticatedUser);
        tokens.setTokenRefresh(jwtTokenRefresh);
        tokens.setTokenAccess(jwtTokenAccess);
        tokens.setExpireTime(jwtService.getExpirationTime());
        tokens.setExpireRefreshTime(jwtRefreshService.getExpirationTime());
        tokenRepository.save(tokens);

        //
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setTokenAccess(jwtTokenAccess);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());
        loginResponse.setUsername(authenticatedUser.getUsername());
        loginResponse.setRole(authenticatedUser.getRole());
        loginResponse.setTokenRefresh(jwtTokenRefresh);
        loginResponse.setExpiresRefreshIn(jwtRefreshService.getExpirationTime());
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }
    @PostMapping("/auth/signin-update")
    public ResponseEntity<?> authenticateUpdate(@RequestBody SignInDTO request) {
        try {
            LoginResponse loginResponse = loginService.login(request);
            return ResponseEntity.ok().body(loginResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Login failed: " + e.getMessage());
        }
    }
}
