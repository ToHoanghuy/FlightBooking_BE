package com.example.FlightBooking.Controller.Auth.ForgotPassword;

import com.example.FlightBooking.DTOs.Request.Auth.ResetPasswordDTO;
import com.example.FlightBooking.Services.AuthJWT.AuthenticationService;

import com.example.FlightBooking.Services.VerificationService.ResetPasswordService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@Tag(name = "Authentication", description = "APIs for authenticate for user")
public class ResetPasswordController {
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private ResetPasswordService resetPasswordService;

    @PutMapping("/auth/reset-password")
    public ResponseEntity<String> ResetPassword(@RequestBody ResetPasswordDTO resetPasswordRequest){
        String result = resetPasswordService.resetPassword(resetPasswordRequest.getEmail(), resetPasswordRequest.getOtp(), resetPasswordRequest.getNewPassword(), resetPasswordRequest.getConfirmPassword());
        return ResponseEntity.ok(result);
    }

}
