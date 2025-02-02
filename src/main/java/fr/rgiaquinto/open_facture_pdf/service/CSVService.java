package fr.rgiaquinto.open_facture_pdf.service;

import fr.rgiaquinto.open_facture_pdf.model.Enterprise;
import fr.rgiaquinto.open_facture_pdf.model.Invoice;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


import static fr.rgiaquinto.open_facture_pdf.model.CSVHeader.*;

@Component
public class CSVService {
    public static final String CSV_TYPE = "text/csv";
    public static final String FORMAT = "UTF-8";

    public boolean checkCSVFile(MultipartFile csv) {
        return CSV_TYPE.equals(csv.getContentType());
    }

    public List<Invoice> createInvoices(MultipartFile csv) throws IOException {
        InputStream inputStream = csv.getInputStream();
        List<Invoice> invoices = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, FORMAT));
        CSVParser csvParser = new CSVParser(bufferedReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());
        List<CSVRecord> csvRecords = csvParser.getRecords();
        for (CSVRecord csvRecord : csvRecords) {
            invoices.add(this.buildInvoiceFromCSVRecord(csvRecord));
        }
        return invoices;
    }

    public Invoice buildInvoiceFromCSVRecord(CSVRecord csvRecord) {
        Invoice invoice = new Invoice();
        Enterprise enterprise = new Enterprise();

        invoice.setClientName(csvRecord.get(CLIENT_NAME));
        invoice.setClientAddress(csvRecord.get(CLIENT_ADDRESS));
        invoice.setClientContact(csvRecord.get(CLIENT_CONTACT));

        enterprise.setName(csvRecord.get(ENTERPRISE_NAME));
        enterprise.setSiren(csvRecord.get(ENTERPRISE_SIREN));
        enterprise.setAddress(csvRecord.get(ENTERPRISE_ADDRESS));
        enterprise.setVatMatriculation(csvRecord.get(ENTERPRISE_VAT_MATRICULATION));
        enterprise.setContact(csvRecord.get(ENTERPRISE_CONTACT));
        invoice.setEnterprise(enterprise);

        invoice.setInvoiceNumber(csvRecord.get(INVOICE_NUMBER));
        invoice.setInvoiceDate(csvRecord.get(INVOICE_DATE));
        invoice.setService(csvRecord.get(SERVICE));
        invoice.setPayments(csvRecord.get(PAYMENTS));
        invoice.setAmountNoTaxes(csvRecord.get(AMOUNT_NO_TAXES).trim().isEmpty() ? 0D : Double.parseDouble(csvRecord.get(AMOUNT_NO_TAXES)));
        invoice.setQuantity(csvRecord.get(QUANTITY).trim().isEmpty() ? 1 : Integer.parseInt(csvRecord.get(QUANTITY)));
        invoice.setVat(csvRecord.get(VAT).trim().isEmpty() ? 0 : Double.parseDouble(csvRecord.get(VAT)));
        invoice.setDiscount(csvRecord.get(DISCOUNT).trim().isEmpty() ? 0 : Double.parseDouble(csvRecord.get(DISCOUNT)));

        invoice.computeAmountWithTaxes();

        return invoice;
    }
}
