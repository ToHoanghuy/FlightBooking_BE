package com.example.FlightBooking.Utils;

import com.example.FlightBooking.Models.Verifications;
import com.example.FlightBooking.Repositories.UserRepository;
import com.example.FlightBooking.Repositories.VerificationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.temporal.ChronoUnit;
@Component
@Hidden
public class EmailUtils {
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private OtpUtils otpUtil;
    @Autowired
    private VerificationRepository verificationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SpringTemplateEngine templateEngine;

    @Transactional
    public void sendSetPasswordEmail (String email) throws MessagingException
    {
        Long otp = Long.valueOf(otpUtil.generateOtp());
        saveOTPInDatabase(email, otp);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("Set Password");
        String htmlMsg = """
                <html>
                <body>
                    <div style="font-family: Arial, sans-serif; line-height: 1.6; padding: 20px; border: 1px solid #ddd; border-radius: 10px; max-width: 600px; margin: auto;">
                        <h2 style="text-align: center; color: #4CAF50;">Set Password</h2>
                        <p>Hello,</p>
                        <p>Your OTP for password change is:</p>
                        <div style="text-align: center; font-size: 24px; font-weight: bold; color: #333;">%s</div>
                        <p>This OTP is valid for 5 minutes.</p>
                        <p>If you did not request a password change, please ignore this email.</p>
                        <p>Thank you!</p>
                        <hr style="border: none; border-top: 1px solid #eee;">
                        <div style="text-align: center; color: #999; font-size: 12px;">
                            <p>FlightBooking Team</p>
                            <p>Do not reply to this email. If you have any questions, contact us at support@flightbooking.com.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(otp);
        mimeMessageHelper.setText(htmlMsg, true);
        javaMailSender.send(mimeMessage);
    }
    private void saveOTPInDatabase(String email, Long otp) {
        LocalDateTime expireTime = LocalDateTime.now().plus(5, ChronoUnit.MINUTES);
        Verifications otpVerification = new Verifications();
        otpVerification.setEmail(email);
        otpVerification.setCodeOTP(otp);
        otpVerification.setExpireTime(expireTime);
        verificationRepository.save(otpVerification);
    }
    @Transactional
    public void sendVoucherEmail(String email, String voucherName, String voucherCode, String discountAmount, String validUntil, String voucherImageUrl) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "utf-8");
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("New Voucher Available!");

        Context context = new Context();
        context.setVariable("voucherName", voucherName);
        context.setVariable("voucherCode", voucherCode);
        context.setVariable("discountAmount", discountAmount);
        context.setVariable("validUntil", validUntil);
        context.setVariable("voucherImageUrl", voucherImageUrl);

        String htmlContent = templateEngine.process("index", context);
        mimeMessageHelper.setText(htmlContent, true);
        javaMailSender.send(mimeMessage);
    }
}
