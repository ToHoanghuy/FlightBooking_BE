package com.example.FlightBooking.Components.TemplateMethod;

import org.springframework.stereotype.Component;

@Component
public class FlightScheduleEmailSender extends AbstractEmailSender{
    @Override
    protected String getSubject() {
        return "Flight Schedule Notification";
    }

    @Override
    protected String getBody(String reason) {
        return "<p>Dear Customer,</p>" +
                "<p>We are pleased to inform you that your flight schedule has been updated:</p>" +
                "<p><strong>" + reason + "</strong></p>" +
                "<p>We look forward to serving you.</p>" +
                "<p>Best regards,</p>" +
                "<p>Your Airline</p>";
    }
}
