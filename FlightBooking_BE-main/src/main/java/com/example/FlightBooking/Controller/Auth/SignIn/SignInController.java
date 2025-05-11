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
import com.example.FlightBooking.Services.LoggingService.AuthLogService;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

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
    private final AuthLogService authLogService;
    
    public SignInController(
            JwtService jwtService, 
            JwtRefreshService jwtRefreshService, 
            AuthenticationService authenticationService, 
            TokenRepository tokenRepository,
            AuthLogService authLogService) {
        this.jwtService = jwtService;
        this.jwtRefreshService = jwtRefreshService;
        this.authenticationService = authenticationService;
        this.tokenRepository = tokenRepository;
        this.authLogService = authLogService;
    }
    @PostMapping("/auth/signin")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody SignInDTO loginUserDto, HttpServletRequest request) {
        try {
            // Log the login attempt
            Map<String, Object> requestInfo = new HashMap<>();
            requestInfo.put("remoteAddr", request.getRemoteAddr());
            requestInfo.put("userAgent", request.getHeader("User-Agent"));
            requestInfo.put("timestamp", System.currentTimeMillis());
            
            authLogService.logAuthenticationEvent(
                "/auth/signin", 
                loginUserDto.getUsername(),
                requestInfo, 
                "ATTEMPT", 
                "Authentication attempt"
            );
            
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
            
            // Log successful login
            Map<String, Object> responseInfo = new HashMap<>();
            responseInfo.put("remoteAddr", request.getRemoteAddr());
            responseInfo.put("userAgent", request.getHeader("User-Agent"));
            responseInfo.put("role", authenticatedUser.getRole());
            responseInfo.put("timestamp", System.currentTimeMillis());
            
            authLogService.logAuthenticationEvent(
                "/auth/signin", 
                authenticatedUser.getUsername(),
                responseInfo, 
                "SUCCESS", 
                "Authentication successful"
            );
            
            return new ResponseEntity<>(loginResponse, HttpStatus.OK);
        } catch (Exception e) {
            // Log authentication failure
            Map<String, Object> errorInfo = new HashMap<>();
            errorInfo.put("remoteAddr", request.getRemoteAddr());
            errorInfo.put("userAgent", request.getHeader("User-Agent"));
            errorInfo.put("timestamp", System.currentTimeMillis());
            
            authLogService.logAuthenticationFailure(
                "/auth/signin", 
                loginUserDto.getUsername(),
                errorInfo, 
                e.getMessage()
            );
            
            throw e; // Re-throw to let the original error handling take place
        }
    }
    @PostMapping("/auth/signin-update")
    public ResponseEntity<?> authenticateUpdate(@RequestBody SignInDTO request, HttpServletRequest httpRequest) {
        try {
            // Log the login attempt with Chain of Responsibility pattern
            Map<String, Object> requestInfo = new HashMap<>();
            requestInfo.put("remoteAddr", httpRequest.getRemoteAddr());
            requestInfo.put("userAgent", httpRequest.getHeader("User-Agent"));
            requestInfo.put("method", "Chain of Responsibility");
            requestInfo.put("timestamp", System.currentTimeMillis());
            
            authLogService.logAuthenticationEvent(
                "/auth/signin-update", 
                request.getUsername(),
                requestInfo, 
                "ATTEMPT", 
                "Authentication attempt via Chain of Responsibility"
            );
            
            LoginResponse loginResponse = loginService.login(request);
            
            // Log successful login
            Map<String, Object> responseInfo = new HashMap<>();
            responseInfo.put("remoteAddr", httpRequest.getRemoteAddr());
            responseInfo.put("userAgent", httpRequest.getHeader("User-Agent"));
            responseInfo.put("role", loginResponse.getRole());
            responseInfo.put("timestamp", System.currentTimeMillis());
            
            authLogService.logAuthenticationEvent(
                "/auth/signin-update", 
                loginResponse.getUsername(),
                responseInfo, 
                "SUCCESS", 
                "Authentication successful via Chain of Responsibility"
            );
            
            return ResponseEntity.ok().body(loginResponse);
        } catch (Exception e) {
            // Log authentication failure
            Map<String, Object> errorInfo = new HashMap<>();
            errorInfo.put("remoteAddr", httpRequest.getRemoteAddr());
            errorInfo.put("userAgent", httpRequest.getHeader("User-Agent"));
            errorInfo.put("timestamp", System.currentTimeMillis());
            
            authLogService.logAuthenticationFailure(
                "/auth/signin-update", 
                request.getUsername(),
                errorInfo, 
                e.getMessage()
            );
            
            return ResponseEntity.badRequest().body("Login failed: " + e.getMessage());
        }
    }
}
