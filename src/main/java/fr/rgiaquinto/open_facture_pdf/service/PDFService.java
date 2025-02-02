package fr.rgiaquinto.open_facture_pdf.service;

import fr.rgiaquinto.open_facture_pdf.model.Invoice;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class PDFService {

    private final int MARGIN = 40;
    private final int MARGIN_TOP = 60;

    private final PDFont FONT_BOLD = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
    private final PDFont FONT_NORMAL = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    private final int TEXT_SIZE = 8;
    private final int SPACE_UNDER_TEXT = 12;
    private final int SPACE_UNDER_SECTION_TEXT = 24;

    public byte[] buildPDFFile(Invoice invoice) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();

        this.buildPDF(invoice, document, page);

        document.addPage(page);
        document.save(outputStream);
        document.close();
        outputStream.close();
        return outputStream.toByteArray();
    }

    public byte[] zipPDFFiles(Map<String, byte[]> pdfs) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);
        for (Map.Entry<String, byte[]> entry : pdfs.entrySet()) {
            String filename = entry.getKey();
            byte[] pdf = entry.getValue();
            ZipEntry zipEntry = new ZipEntry(filename + ".pdf");
            zipEntry.setSize(pdf.length);
            zipOutputStream.putNextEntry(zipEntry);
            zipOutputStream.write(pdf);
            zipOutputStream.closeEntry();
        }
        zipOutputStream.close();
        byteArrayOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    private void buildPDF(Invoice invoice, PDDocument document, PDPage page) throws IOException {
        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        //ENTERPRISE NAME
        contentStream.beginText();
        contentStream.setFont(FONT_BOLD, TEXT_SIZE);

        float namePositionY = page.getBBox().getHeight() - MARGIN_TOP;

        contentStream.newLineAtOffset(MARGIN, namePositionY);
        contentStream.showText(invoice.getEnterprise().getName());
        contentStream.endText();

        //ENTERPRISE ADDRESS
        contentStream.beginText();
        contentStream.setFont(FONT_NORMAL, TEXT_SIZE);

        float addressPositionY = namePositionY - SPACE_UNDER_TEXT;

        contentStream.newLineAtOffset(MARGIN, addressPositionY);
        contentStream.showText(invoice.getEnterprise().getAddress());
        contentStream.endText();

        //ENTERPRISE CONTACT
        contentStream.beginText();
        contentStream.setFont(FONT_NORMAL, TEXT_SIZE);

        float contactPositionY = addressPositionY - SPACE_UNDER_TEXT;

        contentStream.newLineAtOffset(MARGIN, contactPositionY);
        contentStream.showText(invoice.getEnterprise().getContact());
        contentStream.endText();

        //ENTERPRISE SIREN
        contentStream.beginText();
        contentStream.setFont(FONT_BOLD, TEXT_SIZE);

        float sirenTitlePositionY = contactPositionY - SPACE_UNDER_SECTION_TEXT;

        contentStream.newLineAtOffset(MARGIN, sirenTitlePositionY);
        contentStream.showText("N° SIRET/SIREN");
        contentStream.endText();

        contentStream.beginText();
        contentStream.setFont(FONT_NORMAL, TEXT_SIZE);

        float sirenPositionY = sirenTitlePositionY - SPACE_UNDER_TEXT;

        contentStream.newLineAtOffset(MARGIN, sirenPositionY);
        contentStream.showText(invoice.getEnterprise().getSiren());
        contentStream.endText();

        //ENTERPRISE VAT NUMBER
        contentStream.beginText();
        contentStream.setFont(FONT_BOLD, TEXT_SIZE);

        float vatTitlePositionY = sirenPositionY - SPACE_UNDER_SECTION_TEXT;

        contentStream.newLineAtOffset(MARGIN, vatTitlePositionY);
        contentStream.showText("N° TVA intracommunautaire");
        contentStream.endText();

        contentStream.beginText();
        contentStream.setFont(FONT_NORMAL, TEXT_SIZE);

        float vatPositionY = vatTitlePositionY - SPACE_UNDER_TEXT;

        contentStream.newLineAtOffset(MARGIN, vatPositionY);
        contentStream.showText(invoice.getEnterprise().getVatMatriculation() != null && !invoice.getEnterprise().getVatMatriculation().trim().isEmpty() ? invoice.getEnterprise().getVatMatriculation() : "Non éligible à la TVA");
        contentStream.endText();

        //INVOICE NUMBER
        contentStream.beginText();
        contentStream.setFont(FONT_BOLD, TEXT_SIZE);

        float invoiceNumberTextWidth = FONT_BOLD.getStringWidth(invoice.getInvoiceNumber()) / 1000 * TEXT_SIZE;

        float invoiceNumberPositionX = page.getBBox().getUpperRightX() - MARGIN - invoiceNumberTextWidth;
        float invoiceNumberPositionY = page.getBBox().getHeight() - MARGIN_TOP;

        contentStream.newLineAtOffset(invoiceNumberPositionX, invoiceNumberPositionY);
        contentStream.showText(invoice.getInvoiceNumber());
        contentStream.endText();

        //CLIENT SECTION

        float strokeBeginPositionX = MARGIN;
        float strokeBeginPositionY = vatPositionY - SPACE_UNDER_SECTION_TEXT;

        contentStream.moveTo(strokeBeginPositionX, strokeBeginPositionY);

        float strokeEndPositionX = page.getBBox().getUpperRightX() - MARGIN;
        float strokeEndPositionY = strokeBeginPositionY;

        contentStream.lineTo(strokeEndPositionX, strokeEndPositionY);

        contentStream.stroke();

        //CLIENT TITLE
        float clientTextPositionX = MARGIN;
        float clientTextPositionY = strokeBeginPositionY - SPACE_UNDER_SECTION_TEXT;

        contentStream.beginText();
        contentStream.setFont(FONT_BOLD, TEXT_SIZE);
        contentStream.newLineAtOffset(clientTextPositionX, clientTextPositionY);

        contentStream.showText("Client");
        contentStream.endText();

        //CLIENT NAME
        float clientNamePositionX = clientTextPositionX + 100;
        float clientNamePositionY = clientTextPositionY;

        contentStream.beginText();
        contentStream.setFont(FONT_NORMAL, TEXT_SIZE);
        contentStream.newLineAtOffset(clientNamePositionX, clientNamePositionY);
        contentStream.showText(invoice.getClientName());
        contentStream.endText();

        //CLIENT ADDRESS
        float clientAddressPositionX = clientNamePositionX;
        float clientAddressPositionY = clientNamePositionY - SPACE_UNDER_TEXT;

        contentStream.beginText();
        contentStream.setFont(FONT_NORMAL, TEXT_SIZE);
        contentStream.newLineAtOffset(clientAddressPositionX, clientAddressPositionY);
        contentStream.showText(invoice.getClientAddress());
        contentStream.endText();

        //CLIENT CONTACT
        float clientContactPositionX = clientAddressPositionX;
        float clientContactPositionY = clientAddressPositionY - SPACE_UNDER_TEXT;

        contentStream.beginText();
        contentStream.setFont(FONT_NORMAL, TEXT_SIZE);
        contentStream.newLineAtOffset(clientContactPositionX, clientContactPositionY);
        contentStream.showText(invoice.getClientContact());
        contentStream.endText();

        //INVOICE NUMBER TEXT RIGHT
        float invoiceNumberTextRightPositionX = 380;
        float invoiceNumberTextRightPositionY = clientNamePositionY;

        contentStream.beginText();
        contentStream.setFont(FONT_BOLD, TEXT_SIZE);
        contentStream.newLineAtOffset(invoiceNumberTextRightPositionX, invoiceNumberTextRightPositionY);
        contentStream.showText("N° facture");
        contentStream.endText();

        //INVOICE NUMBER TEXT RIGHT

        float invoiceNumberRightPositionX = page.getBBox().getUpperRightX() - MARGIN - invoiceNumberTextWidth;
        float invoiceNumberRightPositionY = invoiceNumberTextRightPositionY;

        contentStream.beginText();
        contentStream.setFont(FONT_NORMAL, TEXT_SIZE);
        contentStream.newLineAtOffset(invoiceNumberRightPositionX, invoiceNumberRightPositionY);
        contentStream.showText(invoice.getInvoiceNumber());
        contentStream.endText();


        //INVOICE DATE TEXT

        float invoiceDateTextPositionX = invoiceNumberTextRightPositionX;
        float invoiceDateTextPositionY = invoiceNumberTextRightPositionY - SPACE_UNDER_SECTION_TEXT;

        contentStream.beginText();
        contentStream.setFont(FONT_BOLD, TEXT_SIZE);
        contentStream.newLineAtOffset(invoiceDateTextPositionX, invoiceDateTextPositionY);
        contentStream.showText("Date de facturation");
        contentStream.endText();

        //INVOICE DATE
        float invoiceDateTextWidth = FONT_NORMAL.getStringWidth(invoice.getInvoiceDate()) / 1000 * TEXT_SIZE;

        float invoiceDatePositionX = page.getBBox().getUpperRightX() - MARGIN - invoiceDateTextWidth;
        float invoiceDatePositionY = invoiceDateTextPositionY;

        contentStream.beginText();
        contentStream.setFont(FONT_NORMAL, TEXT_SIZE);
        contentStream.newLineAtOffset(invoiceDatePositionX, invoiceDatePositionY);
        contentStream.showText(invoice.getInvoiceDate());
        contentStream.endText();

        //SERVICE SECTION

        strokeBeginPositionX = MARGIN;
        strokeBeginPositionY = invoiceDatePositionY - SPACE_UNDER_SECTION_TEXT;

        contentStream.moveTo(strokeBeginPositionX, strokeBeginPositionY);

        strokeEndPositionX = page.getBBox().getUpperRightX() - MARGIN;
        strokeEndPositionY = strokeBeginPositionY;

        contentStream.lineTo(strokeEndPositionX, strokeEndPositionY);

        contentStream.stroke();

        //DESCRIPTION TITLE

        float descriptionTitlePositionX = MARGIN;
        float descriptionTitlePositionY = strokeEndPositionY - SPACE_UNDER_SECTION_TEXT;

        contentStream.beginText();
        contentStream.setFont(FONT_BOLD, TEXT_SIZE);
        contentStream.newLineAtOffset(descriptionTitlePositionX, descriptionTitlePositionY);
        contentStream.showText("DESCRIPTION");
        contentStream.endText();

        //DESCRIPTION

        float descriptionPositionX = descriptionTitlePositionX;
        float descriptionPositionY = descriptionTitlePositionY - SPACE_UNDER_SECTION_TEXT;

        contentStream.beginText();
        contentStream.setFont(FONT_NORMAL, TEXT_SIZE);
        contentStream.newLineAtOffset(descriptionPositionX, descriptionPositionY);
        contentStream.showText(invoice.getService());
        contentStream.endText();

        //QUANTITY TITLE

        float quantityTitlePositionX = descriptionTitlePositionX + 200;
        float quantityTitlePositionY = descriptionTitlePositionY;

        contentStream.beginText();
        contentStream.setFont(FONT_BOLD, TEXT_SIZE);
        contentStream.newLineAtOffset(quantityTitlePositionX, quantityTitlePositionY);
        contentStream.showText("QUANTITÉ");
        contentStream.endText();

        //QUANTITY

        float quantityPositionX = quantityTitlePositionX;
        float quantityPositionY = descriptionPositionY;

        contentStream.beginText();
        contentStream.setFont(FONT_NORMAL, TEXT_SIZE);
        contentStream.newLineAtOffset(quantityPositionX, quantityPositionY);
        contentStream.showText(invoice.getQuantity().toString());
        contentStream.endText();

        //PRICE TITLE

        float priceTitlePositionX = quantityTitlePositionX + 100;
        float priceTitlePositionY = quantityTitlePositionY;

        contentStream.beginText();
        contentStream.setFont(FONT_BOLD, TEXT_SIZE);
        contentStream.newLineAtOffset(priceTitlePositionX, priceTitlePositionY);
        contentStream.showText("PRIX");
        contentStream.endText();

        //PRICE

        float pricePositionX = priceTitlePositionX;
        float pricePositionY = quantityPositionY;

        contentStream.beginText();
        contentStream.setFont(FONT_NORMAL, TEXT_SIZE);
        contentStream.newLineAtOffset(pricePositionX, pricePositionY);
        contentStream.showText(invoice.getAmountNoTaxes().toString());
        contentStream.endText();

        //AMOUNT TITLE

        String amountTitle = "MONTANT";
        float amountTitleTextWidth = FONT_BOLD.getStringWidth(amountTitle) / 1000 * TEXT_SIZE;
        float amountTitlePositionX = page.getBBox().getUpperRightX() - MARGIN - amountTitleTextWidth;
        float amountTitlePositionY = quantityTitlePositionY;

        contentStream.beginText();
        contentStream.setFont(FONT_BOLD, TEXT_SIZE);
        contentStream.newLineAtOffset(amountTitlePositionX, amountTitlePositionY);
        contentStream.showText(amountTitle);
        contentStream.endText();

        //AMOUNT

        float amountTextWidth = FONT_NORMAL.getStringWidth(invoice.getTotalAmountNoTaxes().toString()) / 1000 * TEXT_SIZE;
        float amountPositionX = page.getBBox().getUpperRightX() - MARGIN - amountTextWidth;
        float amountPositionY = pricePositionY;

        contentStream.beginText();
        contentStream.setFont(FONT_NORMAL, TEXT_SIZE);
        contentStream.newLineAtOffset(amountPositionX, amountPositionY);
        contentStream.showText(invoice.getTotalAmountNoTaxes().toString());
        contentStream.endText();

        strokeBeginPositionX = MARGIN;
        strokeBeginPositionY = amountPositionY - SPACE_UNDER_SECTION_TEXT;

        contentStream.moveTo(strokeBeginPositionX, strokeBeginPositionY);

        strokeEndPositionX = page.getBBox().getUpperRightX() - MARGIN;
        strokeEndPositionY = strokeBeginPositionY;

        contentStream.lineTo(strokeEndPositionX, strokeEndPositionY);

        contentStream.stroke();

        //TOTAL SECTION

        //SUB TOTAL TEXT
        float subTotalTextPositionX = 400;
        float subTotalTextPositionY = strokeEndPositionY - SPACE_UNDER_SECTION_TEXT;

        contentStream.beginText();
        contentStream.setFont(FONT_NORMAL, TEXT_SIZE);
        contentStream.newLineAtOffset(subTotalTextPositionX, subTotalTextPositionY);
        contentStream.showText("SOUS TOTAL HT");
        contentStream.endText();

        //SUB TOTAL
        float subTotalTextWidth = FONT_NORMAL.getStringWidth(invoice.getAmountNoTaxes().toString()) / 1000 * TEXT_SIZE;
        float subTotalPositionX = page.getBBox().getUpperRightX() - MARGIN - subTotalTextWidth;
        float subTotalPositionY = subTotalTextPositionY;

        contentStream.beginText();
        contentStream.setFont(FONT_NORMAL, TEXT_SIZE);
        contentStream.newLineAtOffset(subTotalPositionX, subTotalPositionY);
        contentStream.showText(invoice.getAmountNoTaxes().toString());
        contentStream.endText();

        //VAT TEXT
        float vatTextPositionX = subTotalTextPositionX;
        float vatTextPositionY = subTotalTextPositionY - SPACE_UNDER_SECTION_TEXT;

        contentStream.beginText();
        contentStream.setFont(FONT_NORMAL, TEXT_SIZE);
        contentStream.newLineAtOffset(vatTextPositionX, vatTextPositionY);
        contentStream.showText(String.format("TVA (%s)", invoice.getVat().toString()));
        contentStream.endText();

        //VAT
        float vatTotalTextWidth = FONT_NORMAL.getStringWidth(invoice.getAmountOfVAT().toString()) / 1000 * TEXT_SIZE;
        float vatValuePositionX = page.getBBox().getUpperRightX() - MARGIN - vatTotalTextWidth;
        float vatValuePositionY = vatTextPositionY;

        contentStream.beginText();
        contentStream.setFont(FONT_NORMAL, TEXT_SIZE);
        contentStream.newLineAtOffset(vatValuePositionX, vatValuePositionY);
        contentStream.showText(invoice.getAmountOfVAT().toString());
        contentStream.endText();

        //DISCOUNT TEXT

        float discountTextPositionX = vatTextPositionX;
        float discountTextPositionY = vatTextPositionY - SPACE_UNDER_SECTION_TEXT;

        contentStream.beginText();
        contentStream.setFont(FONT_NORMAL, TEXT_SIZE);
        contentStream.newLineAtOffset(discountTextPositionX, discountTextPositionY);
        contentStream.showText("REMISE");
        contentStream.endText();

        //DISCOUNT
        float discountTextWidth = FONT_NORMAL.getStringWidth(invoice.getDiscount().toString()) / 1000 * TEXT_SIZE;
        float discountPositionX = page.getBBox().getUpperRightX() - MARGIN - discountTextWidth;
        float discountPositionY = discountTextPositionY;

        contentStream.beginText();
        contentStream.setFont(FONT_NORMAL, TEXT_SIZE);
        contentStream.newLineAtOffset(discountPositionX, discountPositionY);
        contentStream.showText(invoice.getDiscount().toString());
        contentStream.endText();

        //TOTAL AMOUNT EUR TEXT

        float totalAmountEURTextPositionX = discountTextPositionX;
        float totalAmountEURTextPositionY = discountTextPositionY - SPACE_UNDER_SECTION_TEXT;

        contentStream.beginText();
        contentStream.setFont(FONT_BOLD, TEXT_SIZE);
        contentStream.newLineAtOffset(totalAmountEURTextPositionX, totalAmountEURTextPositionY);
        contentStream.showText("MONTANT TOTAL EUR");
        contentStream.endText();

        //TOTAL AMOUNT EUR
        float totalAmountEURTextWidth = FONT_NORMAL.getStringWidth(invoice.getTotalAmountWithTaxes().toString()) / 1000 * TEXT_SIZE;
        float totalAmountEURPositionX = page.getBBox().getUpperRightX() - MARGIN - totalAmountEURTextWidth;;
        float totalAmountEURPositionY = totalAmountEURTextPositionY;

        contentStream.beginText();
        contentStream.setFont(FONT_BOLD, TEXT_SIZE);
        contentStream.newLineAtOffset(totalAmountEURPositionX, totalAmountEURPositionY);
        contentStream.showText(invoice.getTotalAmountWithTaxes().toString());
        contentStream.endText();

        //PAYMENT METHOD TEXT

        float paymentMethodTextPositionX = MARGIN;
        float paymentMethodTextPositionY = subTotalTextPositionY;

        contentStream.beginText();
        contentStream.setFont(FONT_BOLD, TEXT_SIZE);
        contentStream.newLineAtOffset(paymentMethodTextPositionX, paymentMethodTextPositionY);
        contentStream.showText("Moyens de paiment");
        contentStream.endText();

        //PAYMENT METHOD

        float paymentMethodPositionX = paymentMethodTextPositionX + 100;
        float paymentMethodPositionY = subTotalTextPositionY;

        contentStream.beginText();
        contentStream.setFont(FONT_NORMAL, TEXT_SIZE);
        contentStream.newLineAtOffset(paymentMethodPositionX, paymentMethodPositionY);
        contentStream.showText(invoice.getPayments());
        contentStream.endText();

        //CGU
        float cguPositionX = MARGIN;
        float cguPositionY = totalAmountEURPositionY - SPACE_UNDER_SECTION_TEXT * 3;

        contentStream.beginText();
        contentStream.setFont(FONT_BOLD, TEXT_SIZE);
        contentStream.newLineAtOffset(cguPositionX, cguPositionY);
        contentStream.showText("Conditions générales:");
        contentStream.endText();
        cguPositionY -= SPACE_UNDER_TEXT;
        contentStream.beginText();
        contentStream.setFont(FONT_NORMAL, TEXT_SIZE);
        contentStream.newLineAtOffset(cguPositionX, cguPositionY);
        contentStream.showText("Le réglement doit intervenir au terme de la prestation, sans délais.");
        contentStream.endText();
        cguPositionY -= SPACE_UNDER_TEXT;
        contentStream.beginText();
        contentStream.setFont(FONT_NORMAL, TEXT_SIZE);
        contentStream.newLineAtOffset(cguPositionX, cguPositionY);
        contentStream.showText("Modalités de paiement: cb, virement, espèces, chèques ou paypal.");
        contentStream.endText();
        if (invoice.getVat() == null || invoice.getVat() <= 0) {
            cguPositionY -= SPACE_UNDER_TEXT;
            contentStream.beginText();
            contentStream.setFont(FONT_NORMAL, TEXT_SIZE);
            contentStream.newLineAtOffset(cguPositionX, cguPositionY);
            contentStream.showText("TVA non applicable, article 293B du code général des impôts.");
            contentStream.endText();
        }
        //CLOSE STREAM
        contentStream.close();
    }
}
