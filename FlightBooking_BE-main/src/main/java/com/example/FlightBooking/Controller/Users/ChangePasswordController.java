package com.example.FlightBooking.Controller.Users;

import com.example.FlightBooking.DTOs.Request.User.ChangePasswordRequest;
import com.example.FlightBooking.Models.Users;
import com.example.FlightBooking.Repositories.UserRepository;
import com.example.FlightBooking.Services.AuthJWT.JwtService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@CrossOrigin
@Tag(name ="User Profile", description = "apis for changing user profile and information")
public class ChangePasswordController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;  // Use BCryptPasswordEncoder

    @PostMapping("/users/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        String token = request.getToken();
        String oldPassword = request.getOldPassword();
        String newPassword = request.getNewPassword();

        try {
            String username = jwtService.getUsername(token);

            Optional<Users> optionalUser = userRepository.findByUsername(username);
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }

            Users user = optionalUser.get();
            System.out.println("Found user: " + user.getUsername()); // Log the found user
            System.out.println("Old password entered: " + oldPassword); // Log the old password entered
            System.out.println("Stored hashed password: " + user.getPassword()); // Log the stored hashed password
            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Old password does not match.");
            }

            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return ResponseEntity.ok("Password successfully updated.");
        }
        catch (Exception e) {
            // Log the exception and provide a generic error message
            System.out.println("Error changing password: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("You need to login again - Your token has been expired");
        }
    }
}
