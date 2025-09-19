package com.coworking.backend.service;

import com.coworking.backend.dto.FactureDTO;
import com.coworking.backend.exception.ResourceNotFoundException;
import com.coworking.backend.model.*;
import com.coworking.backend.repository.FactureRepository;
import com.coworking.backend.repository.PaiementRepository;
import com.coworking.backend.util.EmailService;
import com.coworking.backend.util.PdfGeneratorService;
import com.lowagie.text.DocumentException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des factures.
 * Gère la génération de PDF et l'envoi par email.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class FactureService {

    private final FactureRepository factureRepository;
    private final PaiementRepository paiementRepository;
    private final ModelMapper modelMapper;
    private final PdfGeneratorService pdfGeneratorService;
    private final EmailService emailService;
    
    @Value("${app.base-url}") 
    private String baseUrl;

    /**
     * Récupère toutes les factures
     */
    public List<FactureDTO> getAllFactures() {
        return factureRepository.findAll().stream()
                .map(facture -> modelMapper.map(facture, FactureDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Récupère une facture par son ID
     */
    public FactureDTO getFactureById(Long id) {
        Facture facture = factureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facture not found"));
        return modelMapper.map(facture, FactureDTO.class);
    }

    /**
     * Crée une nouvelle facture avec génération de PDF et envoi par email
     * @throws DocumentException en cas d'erreur de génération du PDF
     */
    public FactureDTO createFacture(FactureDTO factureDTO) throws DocumentException {
        Paiement paiement = paiementRepository.findById(factureDTO.getPaiementId())
                .orElseThrow(() -> new ResourceNotFoundException("Paiement not found"));
        
        // Générer le PDF (mais ne pas le stocker)
        ByteArrayOutputStream pdfStream = pdfGeneratorService.generateFacturePdf(paiement);
        
        // Utiliser l'URL de l'API pour le téléchargement dynamique
        String pdfUrl = baseUrl + "/api/factures/" + factureDTO.getPaiementId() + "/pdf";
        
        Facture facture = modelMapper.map(factureDTO, Facture.class);
        facture.setPaiement(paiement);
        facture.setPdfUrl(pdfUrl);
        facture.setDateEnvoi(LocalDateTime.now());
        
        Facture savedFacture = factureRepository.save(facture);
        
        // Envoyer l'email avec la pièce jointe
        emailService.sendEmailWithAttachment(
                facture.getEmailDestinataire(),
                "Votre facture Coworking Space",
                "Veuillez trouver ci-joint votre facture.",
                pdfStream.toByteArray(),
                "facture_" + paiement.getId() + ".pdf"
        );
        
        return modelMapper.map(savedFacture, FactureDTO.class);
    }

    /**
     * Supprime une facture
     */
    public void deleteFacture(Long id) {
        if (!factureRepository.existsById(id)) {
            throw new ResourceNotFoundException("Facture not found");
        }
        factureRepository.deleteById(id);
    }
}