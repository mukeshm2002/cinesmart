package com.mk.cinesmart.service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

@Service
public class EmailService {

    @Value("${SENDGRID_API_KEY}")
    private String sendGridApiKey;

    private static final String FROM_EMAIL = "projectjcu21@gmail.com";

    // 1. WELCOME EMAIL (OTP)
    public void sendWelcomeEmail(String toEmail, String otp) {
        Email from = new Email(FROM_EMAIL);
        Email to = new Email(toEmail);
        String subject = "Welcome to CineSmart! - OTP Verification";
        Content content = new Content("text/plain", "வணக்கம்! CineSmart-ல் இணைந்தமைக்கு நன்றி. உங்கள் சரிபார்ப்பு குறியீடு (OTP): " + otp);

        Mail mail = new Mail(from, subject, to, content);
        sendViaApi(mail);
    }

    public void sendTicketEmail(String toEmail, String subject, String body, String filePath) {
        try {
            java.io.File file = new java.io.File(filePath);
            if (!file.exists()) return;

            byte[] fileBytes = java.nio.file.Files.readAllBytes(file.toPath());
            String encodedFile = java.util.Base64.getEncoder().encodeToString(fileBytes);

            // எளிய முறையில் API-க்கு JSON-ஐ அனுப்பலாம்
            String jsonPayload = "{" +
                    "\"personalizations\": [{\"to\": [{\"email\": \"" + toEmail + "\"}]}], " +
                    "\"from\": {\"email\": \"projectjcu21@gmail.com\"}, " +
                    "\"subject\": \"" + subject + "\", " +
                    "\"content\": [{\"type\": \"text/plain\", \"value\": \"" + body + "\"}], " +
                    "\"attachments\": [{" +
                    "\"content\": \"" + encodedFile + "\", " +
                    "\"type\": \"image/png\", " +
                    "\"filename\": \"Ticket.png\", " +
                    "\"disposition\": \"attachment\"" +
                    "}]" +
                    "}";

            SendGrid sg = new SendGrid(sendGridApiKey);
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(jsonPayload); // இங்கே நேரடியாக JSON-ஐ அனுப்புகிறோம்
            sg.api(request);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // API Helper Method
    private void sendViaApi(Mail mail) {
        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            sg.api(request);
        } catch (IOException e) {
            System.err.println("SendGrid API Error: " + e.getMessage());
        }
    }
}