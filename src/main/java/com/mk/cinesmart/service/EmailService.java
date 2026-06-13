package com.mk.cinesmart.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    // SendGrid-ல் வெரிஃபை செய்த மெயில் முகவரி
    private static final String SENDER_EMAIL = "projectjcu21@gmail.com";

    public void sendWelcomeEmail(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(SENDER_EMAIL);
            message.setTo(toEmail);
            message.setSubject("Welcome to CineSmart! - OTP Verification");
            message.setText("வணக்கம்! CineSmart-ல் இணைந்தமைக்கு நன்றி. உங்கள் சரிபார்ப்பு குறியீடு (OTP): " + otp);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("!!! Welcome மின்னஞ்சல் அனுப்ப முடியவில்லை: " + e.getMessage());
        }
    }

    public void sendTicketEmail(String toEmail, String subject, String body, String filePath) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(SENDER_EMAIL);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body);

            java.io.File file = new java.io.File(filePath);
            if(file.exists()) {
                helper.addAttachment("MyTicket.png", new FileSystemResource(file));
            }

            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Ticket மின்னஞ்சல் அனுப்ப முடியவில்லை: " + e.getMessage());
        }
    }
}
