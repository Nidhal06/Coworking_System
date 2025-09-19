package com.coworking.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * DTO pour les factures
 */
@Data
public class FactureDTO {
    private Long id;
    private Long paiementId;
    private String pdfUrl;
    private LocalDateTime dateEnvoi;
    private String emailDestinataire;
}