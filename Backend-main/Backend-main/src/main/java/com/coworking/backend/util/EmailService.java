package com.coworking.backend.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;

/**
 * Service pour l'envoi d'emails avec différentes options :
 * - Email simple (HTML/text)
 * - Email avec pièce jointe
 * - Email basé sur un template Thymeleaf
 */
@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    /**
     * Constructeur pour l'injection des dépendances
     * @param mailSender Le service d'envoi d'emails de Spring
     * @param templateEngine Le moteur de templates Thymeleaf
     */
    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    /**
     * Envoie un email simple (HTML/text)
     * @param to Destinataire
     * @param subject Sujet de l'email
     * @param text Contenu de l'email (peut contenir du HTML)
     * @throws RuntimeException si l'envoi échoue
     */
    public void sendSimpleMessage(String to, String subject, String text) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    /**
     * Envoie un email avec une pièce jointe
     * @param to Destinataire
     * @param subject Sujet de l'email
     * @param text Contenu de l'email
     * @param attachment Contenu binaire de la pièce jointe
     * @param attachmentName Nom de la pièce jointe
     * @throws RuntimeException si l'envoi échoue
     */
    public void sendEmailWithAttachment(String to, String subject, String text, 
                                      byte[] attachment, String attachmentName) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);
            helper.addAttachment(attachmentName, () -> new java.io.ByteArrayInputStream(attachment));
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email with attachment", e);
        }
    }

    /**
     * Envoie un email basé sur un template Thymeleaf
     * @param to Destinataire
     * @param subject Sujet de l'email
     * @param templateName Nom du template Thymeleaf (sans extension)
     * @param context Contexte contenant les variables pour le template
     */
    public void sendTemplateEmail(String to, String subject, 
                                String templateName, Context context) {
        String htmlContent = templateEngine.process(templateName, context);
        sendSimpleMessage(to, subject, htmlContent);
    }
}