package com.example.FlightBooking.Components.TemplateMethod;

import com.example.FlightBooking.Services.TemplateMethod.TemplateMethodService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/template-method")
@Tag(name = "Template Method Design Pattern", description = "...")
public class TemplateMethodController {
    @Autowired
    private TemplateMethodService templateMethodService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestParam String email) {
        templateMethodService.registerUser(email);
        return ResponseEntity.ok("OTP sent to email.");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        templateMethodService.requestPasswordReset(email);
        return ResponseEntity.ok("OTP sent to email.");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOTP(@RequestParam String email, @RequestParam Long otp) {
        boolean isValid = templateMethodService.verifyOTP(email, otp);
        return isValid ? ResponseEntity.ok("OTP is valid.") : ResponseEntity.badRequest().body("Invalid OTP.");
    }

    @PostMapping("/book-ticket")
    public ResponseEntity<String> bookTicket(@RequestParam String email, @RequestParam String ticketDetails) {
        templateMethodService.bookTicket(email, ticketDetails);
        return ResponseEntity.ok("Ticket booking email sent.");
    }
}
