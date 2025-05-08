package com.example.FlightBooking.Components.ChainOfResponsibility;

import com.example.FlightBooking.DTOs.Request.Auth.SignInDTO;

public abstract class BaseHandler implements Handler {
    protected Handler next;

    @Override
    public void setNext(Handler next) {
        this.next = next;
    }

    protected void forward(SignInDTO request) throws Exception {
        if (next != null) {
            next.handle(request);
        }
    }
}
