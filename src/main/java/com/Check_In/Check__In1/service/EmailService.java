package com.Check_In.Check__In1.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarCorreoMasivo(List<String> destinatarios, String asunto, String mensaje) {
        for (String email : destinatarios) {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(email);
            mail.setSubject(asunto);
            mail.setText(mensaje);
            mailSender.send(mail);
        }
    }

    // ================= Enviar correo con PDF adjunto =================
    public void enviarCorreoConAdjunto(String destinatario, String asunto, String mensaje, byte[] archivo, String nombreArchivo) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(mensaje);
            helper.addAttachment(nombreArchivo, new ByteArrayResource(archivo));
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
