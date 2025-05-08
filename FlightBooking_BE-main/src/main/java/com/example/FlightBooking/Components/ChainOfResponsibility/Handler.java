package com.example.FlightBooking.Components.ChainOfResponsibility;

import com.example.FlightBooking.DTOs.Request.Auth.SignInDTO;

public interface Handler {
    void setNext(Handler handler);
    void handle(SignInDTO request) throws Exception;
}
