package com.example.FlightBooking.Controller.Auth.Verification;

import com.example.FlightBooking.Services.VerificationService.VerificationService;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@CrossOrigin
@Tag(name = "Authentication", description = "APIs for authenticate for user")
public class VeriPasswordController {
    @Autowired
    VerificationService verificationService;
    @PostMapping("/auth/check-otp")
        public ResponseEntity<String> checkOTP(@RequestParam Long codeOTP, @RequestParam String email) {
            if (verificationService.checkOTP(codeOTP, email)) {
                return ResponseEntity.ok("OTP is valid");
            } else {
                return ResponseEntity.badRequest().body("Invalid OTP or expired");
            }
        }
}
