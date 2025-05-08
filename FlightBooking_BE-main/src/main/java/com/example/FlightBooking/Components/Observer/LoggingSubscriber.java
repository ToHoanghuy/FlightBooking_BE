package com.example.FlightBooking.Components.Observer;

import org.springframework.stereotype.Component;

@Component
public class LoggingSubscriber implements Subscriber {
    @Override
    public void update(String context, String eventType) {
        // Logic ghi lại log
        System.out.println("Logging: Event Type: " + eventType + ", Context: " + context);
    }
}
