package fr.rgiaquinto.open_facture_pdf.controller;

import fr.rgiaquinto.open_facture_pdf.model.Invoice;
import fr.rgiaquinto.open_facture_pdf.service.PDFService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.io.IOException;
import java.util.*;

@Controller
public class ImportController {


    private PDFService pdfService;

    public ImportController(PDFService pdfService) {
        this.pdfService = pdfService;
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
                                .filename("invoices.zip")
                                .build().toString())
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body("Erreur lors de la création des fichiers PDF. Veuillez vérifier vos données de facturation.");
        }
    }
}
