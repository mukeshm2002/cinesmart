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

    // EmailService.java-வில் இந்த மாற்றத்தைச் செய்யவும்
    public void sendWelcomeEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("your-email@gmail.com");
        message.setTo(toEmail);
        message.setSubject("Welcome to CineSmart! - OTP Verification");
        message.setText("வணக்கம்! CineSmart-ல் இணைந்தமைக்கு நன்றி. உங்கள் சரிபார்ப்பு குறியீடு (OTP): " + otp);
        mailSender.send(message);
    }
    public void sendTicketEmail(String toEmail, String subject, String body, String filePath) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("your-email@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body);

            // டிக்கெட் இமேஜை இணைத்தல்
            FileSystemResource file = new FileSystemResource(new File(filePath));
            helper.addAttachment("MyTicket.png", file);

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            // லாக் (Log) செய்யவும்
        }
    }
}
