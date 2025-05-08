package com.example.FlightBooking.Services.TemplateMethod;

import com.example.FlightBooking.Components.TemplateMethod.OtpEmailSender;
import com.example.FlightBooking.Components.TemplateMethod.TicketEmailSender;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TemplateMethodService {
    private final OtpEmailSender otpEmailSender;
    //private final TicketEmailSender ticketEmailSender;

    @Autowired
    public TemplateMethodService(OtpEmailSender otpEmailSender, TicketEmailSender ticketEmailSender) {
        this.otpEmailSender = otpEmailSender;
        //this.ticketEmailSender = ticketEmailSender;
    }
    public void registerUser(String email) {
        try {
            otpEmailSender.sendEmail(email, email);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void requestPasswordReset(String email) {
        try {
            otpEmailSender.sendEmail(email, email);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void bookTicket(String email, String ticketDetails) {
        //ticketEmailSender.sendEmail(email, ticketDetails);
    }

    public boolean verifyOTP(String email, Long inputOTP) {
        return false;
    }
}
