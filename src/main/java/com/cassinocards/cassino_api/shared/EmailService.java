package com.cassinocards.cassino_api.shared;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.base-url}")
    private String baseUrl;

    public void sendVerificationEmail(String toEmail, UUID token) {
        String subject = "Verify your account";
        String verificationLink = this.baseUrl + "/auth/verify?token=" + token;

        String messageText = "Click the link to verify your account:\n" + verificationLink;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(messageText);

        mailSender.send(message);
    }

    public void sendUserCreatedEmail(String toEmail) {
        String subject = "Account created";
        String messageText = "Your account has been created successfully.";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(messageText);

        mailSender.send(message);
    }

    public void sendUserPasswordChangedEmail(String toEmail) {
        String subject = "Password changed";
        String messageText = "Your password has been changed successfully.";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(messageText);

        mailSender.send(message);
    }

    public void sendPasswordResetEmail(String toEmail, UUID token) {
        String subject = "Reset your password";
        String resetLink = this.baseUrl + "/auth/reset-password?token=" + token;

        String messageText = "Click the link to reset your password:\n" + resetLink;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(messageText);

        mailSender.send(message);
    }
}
