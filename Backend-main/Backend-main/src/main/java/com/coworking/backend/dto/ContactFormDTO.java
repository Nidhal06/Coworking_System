package com.coworking.backend.dto;

import lombok.Data;

/**
 * DTO pour le formulaire de contact
 */
@Data
public class ContactFormDTO {
    private String name;
    private String email;
    private String subject;
    private String message;
    private String toEmail;
}