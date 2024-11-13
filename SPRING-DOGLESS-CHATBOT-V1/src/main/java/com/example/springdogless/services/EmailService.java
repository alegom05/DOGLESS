package com.example.springdogless.services;

import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendEmail(String to, String subject, String text) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);

            // Aquí defines el alias que aparecerá como remitente
            helper.setFrom("doglessadmin@gmail.com", "Dogless Admin");

            javaMailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Error al enviar el correo", e);
        }
    }
}
