package com.example.FlightBooking.Components.TemplateMethod;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public abstract class AbstractEmailSender {
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmail(String to, String context) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setSubject(getSubject());
        mimeMessageHelper.setText(getBody(context), true);
        javaMailSender.send(mimeMessage);
    }

    protected abstract String getSubject();
    protected abstract String getBody(String context);
}
