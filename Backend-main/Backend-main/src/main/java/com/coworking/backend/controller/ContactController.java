package com.coworking.backend.controller;

import com.coworking.backend.dto.ContactFormDTO;
import com.coworking.backend.util.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.context.Context;

/**
 * Contrôleur pour la gestion des formulaires de contact
 */
@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
public class ContactController {

    private final EmailService emailService;

    /**
     * Envoie un message de contact par email
     */
    @PostMapping
    public ResponseEntity<Void> sendContactMessage(@RequestBody ContactFormDTO contactForm) {
        Context context = new Context();
        context.setVariable("name", contactForm.getName());
        context.setVariable("email", contactForm.getEmail());
        context.setVariable("subject", contactForm.getSubject());
        context.setVariable("message", contactForm.getMessage());

        emailService.sendTemplateEmail(
            contactForm.getToEmail(),
            "Nouveau message de contact: " + contactForm.getSubject(),
            "email/contact-message",
            context
        );

        return ResponseEntity.ok().build();
    }
}