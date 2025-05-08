package com.example.FlightBooking.Controller.Users;

import com.example.FlightBooking.DTOs.Request.User.UserRequest;
import com.example.FlightBooking.DTOs.Response.User.UserResponse;
import com.example.FlightBooking.Enum.Roles;
import com.example.FlightBooking.Models.Users;
import com.example.FlightBooking.Repositories.UserRepository;
import com.example.FlightBooking.Services.AuthJWT.JwtService;
import com.example.FlightBooking.Services.UserService.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@Tag(name ="User Profile", description = "apis for changing user profile and information")
public class UserInfoController {
    private final UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    public UserInfoController(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    private JwtService jwtService;

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();

        if (users.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(users, HttpStatus.OK);
    }
    @GetMapping("/users/username") // Endpoint to get a user by username
    @Operation(summary = "Get user by username", description = "Retrieve a user profile by their username")
    public ResponseEntity<UserResponse> getUserByUsername(@RequestParam("username") String username) {
        Users user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Username not found"));
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setUsername(username);
        userResponse.setEmail(user.getEmail());
        userResponse.setFullName(user.getFullName());
        userResponse.setRole(user.getRole());
        userResponse.setAvatarUrl(user.getAvatarUrl());
        userResponse.setPersonalId(user.getPersonalId());
        userResponse.setDayOfBirth(user.getDayOfBirth());
        userResponse.setPhoneNumber(user.getPhoneNumber());
        userResponse.setAddress(user.getAddress());
        return ResponseEntity.ok(userResponse);
    }
    @GetMapping("/users/token")
    public ResponseEntity<UserResponse> getUserInfo(@RequestParam("token") String token) {

        String username = jwtService.getUsername(token);

        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Users user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Username not found"));

        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setGender(user.getGender());
        userResponse.setUsername(username);
        userResponse.setEmail(user.getEmail());
        userResponse.setFullName(user.getFullName());
        userResponse.setRole(user.getRole());
        userResponse.setAvatarUrl(user.getAvatarUrl());
        userResponse.setDayOfBirth(user.getDayOfBirth());
        userResponse.setPersonalId(user.getPersonalId());
        userResponse.setPhoneNumber(user.getPhoneNumber());
        userResponse.setAddress(user.getAddress());
        return ResponseEntity.ok(userResponse);
    }
    @PutMapping("/users/{username}")
    public ResponseEntity<Users> updateUser(@PathVariable String username, @RequestBody UserRequest updateRequest) {
        Users updatedUser = userService.updateUser(username, updateRequest);
        return ResponseEntity.ok(updatedUser);
    }
    @GetMapping("/admin/users/by-role")
    public List<Users> getUsersByRole(@RequestParam("EMPLOYEE") Roles role) {
        return userService.getUsersByRole(role);
    }
    @DeleteMapping("/users/delete-user-by-id/{id}")
    public String deleteUserById(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return "User not found by id";
        }
        userRepository.deleteById(id);
        return "Delete user complete";
    }
}
