package com.example.FlightBooking.Controller.Auth.SignUp;

import com.example.FlightBooking.DTOs.Request.Auth.SignUpDTO;
import com.example.FlightBooking.Models.Users;
import com.example.FlightBooking.Services.AuthJWT.AuthenticationService;
import com.example.FlightBooking.Services.AuthJWT.JwtService;
import com.example.FlightBooking.Services.LoggingService.AuthLogService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
@Tag(name = "Authentication", description = "APIs for authenticate for user")
public class SignUpController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final AuthLogService authLogService;

    public SignUpController(
            JwtService jwtService, 
            AuthenticationService authenticationService,
            AuthLogService authLogService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.authLogService = authLogService;
    }
    @PostMapping("/auth/signup")
    public ResponseEntity<Users> register(@RequestBody SignUpDTO registerUserDto, HttpServletRequest request) {
        try {
            // Log the registration attempt
            Map<String, Object> requestInfo = new HashMap<>();
            requestInfo.put("remoteAddr", request.getRemoteAddr());
            requestInfo.put("userAgent", request.getHeader("User-Agent"));
            requestInfo.put("timestamp", System.currentTimeMillis());
            requestInfo.put("email", registerUserDto.getEmail());
            
            authLogService.logAuthenticationEvent(
                "/auth/signup", 
                registerUserDto.getUsername(),
                requestInfo, 
                "REGISTRATION_ATTEMPT", 
                "User registration attempt"
            );
            
            Users registeredUser = authenticationService.signup(registerUserDto);
            
            // Log successful registration
            Map<String, Object> responseInfo = new HashMap<>();
            responseInfo.put("remoteAddr", request.getRemoteAddr());
            responseInfo.put("userAgent", request.getHeader("User-Agent"));
            responseInfo.put("timestamp", System.currentTimeMillis());
            responseInfo.put("email", registeredUser.getEmail());
            responseInfo.put("role", registeredUser.getRole());
            
            authLogService.logAuthenticationEvent(
                "/auth/signup", 
                registeredUser.getUsername(),
                responseInfo, 
                "REGISTRATION_SUCCESS", 
                "User registration successful"
            );
            
            return ResponseEntity.ok(registeredUser);
        } catch (Exception e) {
            // Log registration failure
            Map<String, Object> errorInfo = new HashMap<>();
            errorInfo.put("remoteAddr", request.getRemoteAddr());
            errorInfo.put("userAgent", request.getHeader("User-Agent"));
            errorInfo.put("timestamp", System.currentTimeMillis());
            errorInfo.put("error", e.getMessage());
            
            authLogService.logAuthenticationFailure(
                "/auth/signup", 
                registerUserDto.getUsername(),
                errorInfo, 
                e.getMessage()
            );
            
            throw e; // Re-throw to let the original error handling take place
        }
    }
}
