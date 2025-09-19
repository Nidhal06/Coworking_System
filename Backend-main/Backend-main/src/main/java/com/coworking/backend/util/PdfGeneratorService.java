package com.coworking.backend.util;

import com.coworking.backend.model.Paiement;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

/**
 * Service pour la génération de fichiers PDF (factures)
 */
@Service
public class PdfGeneratorService {

    /**
     * Génère un PDF de facture pour un paiement
     * @param paiement Les informations de paiement à inclure dans la facture
     * @return Flux de sortie contenant le PDF généré
     * @throws DocumentException Si une erreur survient lors de la génération du PDF
     */
    public ByteArrayOutputStream generateFacturePdf(Paiement paiement) throws DocumentException {
        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, outputStream);
        
        document.open();
        
        // Ajout du titre
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("Facture", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        
        // Ajout des détails du paiement
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("Numéro de facture: " + paiement.getId()));
        document.add(new Paragraph("Date: " + paiement.getDate()));
        document.add(new Paragraph("Montant: " + paiement.getMontant() + " TND"));
        document.add(new Paragraph("Statut: " + paiement.getStatut()));
        
        // Ajout des détails spécifiques au type de paiement
        switch (paiement.getType()) {
            case RESERVATION:
                document.add(new Paragraph("Type: Réservation d'espace privé"));
                document.add(new Paragraph("Espace: " + paiement.getReservation().getEspace().getName()));
                document.add(new Paragraph("Période: " + paiement.getReservation().getDateDebut() + " - " + paiement.getReservation().getDateFin()));
                break;
            case ABONNEMENT:
                document.add(new Paragraph("Type: Abonnement espace ouvert"));
                document.add(new Paragraph("Période: " + paiement.getAbonnement().getDateDebut() + " - " + paiement.getAbonnement().getDateFin()));
                break;
            case EVENEMENT:
                document.add(new Paragraph("Type: Participation à événement"));
                document.add(new Paragraph("Événement: " + paiement.getEvenement().getTitre()));
                break;
        }
        
        document.close();
        return outputStream;
    }
}