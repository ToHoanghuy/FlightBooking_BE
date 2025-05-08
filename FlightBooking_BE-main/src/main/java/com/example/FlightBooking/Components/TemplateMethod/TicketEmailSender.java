package com.example.FlightBooking.Components.TemplateMethod;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;

@Component
public class TicketEmailSender {
    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    private JavaMailSender javaMailSender;

    protected String getSubject() {
        return "Your Flight Ticket Details";
    }
    @Transactional
    public void sendTicketToEmail(String email, String timeDeparture, String timeArrival,
                             String airline, String departure, String departureCity,
                             String arrival, String passengerName, String arrivalCity,
                             String boardingTime, String flightNumber, String seat ) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "utf-8");
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("New Ticket Available!");

        String gate = "A01";

        Context context = new Context();
        context.setVariable("airline", airline);
        context.setVariable("departureTime", timeDeparture);
        context.setVariable("arrivalTime", timeArrival);
        context.setVariable("departureCity", departureCity);
        context.setVariable("arrivalCity", arrivalCity);
        context.setVariable("departure", departure);
        context.setVariable("arrival", arrival);
        context.setVariable("passengerName", passengerName);
        context.setVariable("flightNumber", flightNumber);
        context.setVariable("gate", gate);
        context.setVariable("seat", seat);
        context.setVariable("boardingTime", boardingTime);

        String htmlContent = templateEngine.process("ticketsEmail", context);

        mimeMessageHelper.setText(htmlContent, true);
        javaMailSender.send(mimeMessage);
    }
}