package com.example.FlightBooking.Components.Builder;

import com.example.FlightBooking.Enum.Roles;
import com.example.FlightBooking.Models.Users;

import java.sql.Timestamp;

public interface UserBuilder {
    void reset();
    void setUsername(String username);
    void setPassword(String password);
    void setEmail(String email);
    void setRole(Roles role);
    void setFullName(String fullName);
    void setDayOfBirth(Timestamp dayOfBirth);
    Users build();
}
