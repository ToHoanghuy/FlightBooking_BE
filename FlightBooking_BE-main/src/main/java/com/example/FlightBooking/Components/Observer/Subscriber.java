package com.example.FlightBooking.Components.Observer;

public interface Subscriber {
    void update(String context, String eventType);
}