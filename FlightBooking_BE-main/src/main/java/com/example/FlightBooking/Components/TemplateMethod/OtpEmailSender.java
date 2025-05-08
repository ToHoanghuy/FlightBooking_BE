package com.example.FlightBooking.Components.TemplateMethod;

import com.example.FlightBooking.Models.Verifications;
import com.example.FlightBooking.Repositories.VerificationRepository;
import com.example.FlightBooking.Utils.OtpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class OtpEmailSender extends AbstractEmailSender {
    @Autowired
    private OtpUtils otpUtils;

    @Autowired
    private VerificationRepository verificationRepository;

    @Override
    protected String getSubject() {
        return "Your OTP Code";
    }

    @Override
    protected String getBody(String email) {
        Long otp = Long.valueOf(otpUtils.generateOtp());
        saveOTPInDatabase(email, otp);
        return """
                <html>
                <body>
                    <div style="font-family: Arial, sans-serif; line-height: 1.6; padding: 20px; border: 1px solid #ddd; border-radius: 10px; max-width: 600px; margin: auto;">
                        <h2 style="text-align: center; color: #4CAF50;">Your OTP Code</h2>
                        <p>Hello,</p>
                        <p>Your OTP code is:</p>
                        <div style="text-align: center; font-size: 24px; font-weight: bold; color: #333;">%s</div>
                        <p>This OTP is valid for 5 minutes.</p>
                        <p>If you did not request this code, please ignore this email.</p>
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
    }

    private void saveOTPInDatabase(String email, Long otp) {
        LocalDateTime expireTime = LocalDateTime.now().plus(5, ChronoUnit.MINUTES);
        var otpVerification = new Verifications();
        otpVerification.setEmail(email);
        otpVerification.setCodeOTP(otp);
        otpVerification.setExpireTime(expireTime);
        verificationRepository.save(otpVerification);
    }
}