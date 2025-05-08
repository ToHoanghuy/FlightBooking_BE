package com.example.FlightBooking.Services.VerificationService;

import com.example.FlightBooking.Models.Users;
import com.example.FlightBooking.Repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ResetPasswordService {

    private final UserRepository userRepository;
    private final VerificationService verificationService;
    private final PasswordEncoder passwordEncoder;

    public ResetPasswordService(UserRepository userRepository, VerificationService verificationService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.verificationService = verificationService;
        this.passwordEncoder = passwordEncoder;
    }

    public String resetPassword(String email, Long otp, String newPassword, String confirmPassword) {
        Users user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found with this email: " + email));

        if (!verificationService.checkOTP(otp, email)) {
            return "OTP wrong! Please fill again";
        }

        if (!newPassword.equals(confirmPassword)) {
            return "Password do not match! Please fill again";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return "Successful! Password was reset";
    }
}

