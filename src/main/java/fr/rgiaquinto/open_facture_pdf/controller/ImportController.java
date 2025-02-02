package fr.rgiaquinto.open_facture_pdf.controller;

import fr.rgiaquinto.open_facture_pdf.model.Invoice;
import fr.rgiaquinto.open_facture_pdf.service.CSVService;
import fr.rgiaquinto.open_facture_pdf.service.PDFService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Controller
@RequestMapping("/api")
public class ImportController {

    private CSVService csvService;

    private PDFService pdfService;

    public ImportController(CSVService csvService, PDFService pdfService) {
        this.csvService = csvService;
        this.pdfService = pdfService;
    }

    @PostMapping("/invoices")
    public ResponseEntity getInvoices(MultipartFile csv) {
        if (csv == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body("Le fichier CSV est manquant.");
        }
        if (!csvService.checkCSVFile(csv)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body("Le fichier CSV n'est pas valide.");
        }
        List<Invoice> invoices;
        try {
            invoices = csvService.createInvoices(csv);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body("Une erreur est survenue lors de l'extraction des informations de facturation. Veuillez vérifier le fichier CSV.");
        }
        return ResponseEntity.ok(invoices);
    }

    @PostMapping("/import")
    public ResponseEntity importCSV(@RequestBody List<Invoice> invoices) {
        try {
            Map<String, byte[]> pdfs = new HashMap<>();
            for (Invoice invoice : invoices) {
                pdfs.put(invoice.getInvoiceNumber(), pdfService.buildPDFFile(invoice));
            }
            byte[] archive = pdfService.zipPDFFiles(pdfs);
            ByteArrayResource resource = new ByteArrayResource(archive);
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(resource.contentLength())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename("factures.zip")
                                .build().toString())
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body("Erreur lors de la création des fichiers PDF. Veuillez vérifier vos données de facturation.");
        }
    }

    @GetMapping("/sample")
    public ResponseEntity<Resource> getSample() {
        List<String> invoiceHeaders = new ArrayList<>(Arrays.asList(
                "Numéro de facture",
                "Date de facturation",
                "Nom client",
                "Adresse client",
                "Contact client",
                "Prestation facturée",
                "Moyens de paiement",
                "Montant HT",
                "Quantité",
                "TVA %",
                "Remise",
                "Nom entreprise",
                "SIREN",
                "Adresse entreprise",
                "Contact entreprise",
                "N° TVA intracommunautaire"
        ));
        String csvHeaders = String.join(",", invoiceHeaders);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=factures_modele.csv")
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
                .body(new InputStreamResource(new ByteArrayInputStream(csvHeaders.getBytes(StandardCharsets.UTF_8))));
    }

}
