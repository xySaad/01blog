package com.z01.blog.services;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(String to, int token) throws MessagingException, UnsupportedEncodingException {
        String link = "http://localhost:4200/auth?token=" + token;

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

        helper.setTo(to);
        helper.setSubject("Verify your email");
        helper.setFrom(new InternetAddress("saad@rmaida.ga", "srm - 01blog"));

        String text = """
                Click the link to verify: %s
                or use this code:
                %d
                """;

        message.setText(String.format(text, link, token));
        mailSender.send(message);
    }
}