package com.example.FlightBooking.Components.Builder;

import com.example.FlightBooking.Enum.Roles;
import com.example.FlightBooking.Models.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Timestamp;

public class CustomerConcrete implements UserBuilder{
    @Autowired
    private Users user;
    private final PasswordEncoder passwordEncoder;

    public CustomerConcrete(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.reset();
    }

    @Override
    public void reset() {
        this.user = new Users();
    }

    @Override
    public void setUsername(String username) {
        this.user.setUsername(username);
    }

    @Override
    public void setPassword(String password) {
        this.user.setPassword(passwordEncoder.encode(password));
    }

    @Override
    public void setEmail(String email) {
        this.user.setEmail(email);
    }

    @Override
    public void setRole(Roles role) {
        this.user.setRole(Roles.CUSTOMER);
    }
    @Override
    public void setFullName(String fullName)
    {
        this.user.setFullName(fullName);
    }
    @Override
    public void setDayOfBirth(Timestamp dayOfBirth)
    {
        this.user.setDayOfBirth(dayOfBirth);
    }
    @Override
    public Users build() {
        Users builtUser = this.user;
        this.reset(); // Optional: Reset the builder after building the user
        return builtUser;
    }
}
