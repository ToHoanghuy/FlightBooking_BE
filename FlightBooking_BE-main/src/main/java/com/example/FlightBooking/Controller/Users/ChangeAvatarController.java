package com.example.FlightBooking.Controller.Users;

import com.example.FlightBooking.Services.UserService.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@CrossOrigin
@Tag(name ="User Profile", description = "apis for changing user profile and information")
public class ChangeAvatarController {
    @Autowired
    private UserService userService;

    @PostMapping(value = "/users/upload-new-avatar", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> uploadAvatar(@RequestParam("token") String token,
                                               @RequestPart("avatarFile") MultipartFile avatarFile) {
        try {
            String avatarUrl = userService.uploadUserAvatar(token, avatarFile);
            return ResponseEntity.ok(avatarUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
