package com.aditya.ecommerce.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendVerificationEmail(String toEmail, String token) {
        String subject = "Verify your account";
        String verificationUrl = "http://localhost:8080/api/auth/verify?token=" + token;

        String content = """
            <html>
                <body>
                    <p> Thank you for signing up on our ecommerce platform. </p>
                    <p>Click the link below to verify your email:</p>
                    <p><a href="%s">Verify Now</a></p>
                    <p>If the link doesn't work, copy and paste this URL into your browser:</p>
                    <p>%s</p>
                </body>
            </html>
            """.formatted(verificationUrl, verificationUrl);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(content, true); // Important: HTML format


            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send verification email", e);
        }
    }
}
