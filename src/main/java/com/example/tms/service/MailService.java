package com.example.tms.service;

import com.example.tms.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    private static final Logger log = LoggerFactory.getLogger(MailService.class);
    private final JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtpEmail(String to, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Your Tutor Management System OTP");
            message.setText("Your OTP code is: " + otp + ". It expires in 5 minutes.");
            mailSender.send(message);
        } catch (Exception ex) {
            log.error("OTP email sending failed for {}: {}", to, ex.getMessage(), ex);
            throw new ApiException("Failed to deliver OTP email. Please try again later.");
        }
    }
}
