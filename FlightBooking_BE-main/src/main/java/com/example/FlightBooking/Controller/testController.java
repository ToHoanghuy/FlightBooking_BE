package com.example.FlightBooking.Controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin (value = "*")
public class testController {

    @GetMapping("/test")
    public String welcome() {
        return "WELCOME MY API BACKEND SERVER";
    }
}
