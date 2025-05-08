package com.example.FlightBooking.Components.TemplateMethod;

import org.springframework.stereotype.Component;

@Component
public class FlightCancelEmailSender extends AbstractEmailSender{
    @Override
    protected String getSubject() {
        return "Flight Cancellation Notification";
    }

    @Override
    protected String getBody(String reason) {
        return "<p>Dear Customer,</p>" +
                "<p>We regret to inform you that your flight has been canceled due to the following reason:</p>" +
                "<p><strong>" + reason + "</strong></p>" +
                "<p>We apologize for the inconvenience caused.</p>" +
                "<p>Best regards,</p>" +
                "<p>Your Airline</p>";
    }
}
