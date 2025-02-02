package fr.rgiaquinto.open_facture_pdf.model;

public abstract class CSVHeader {
    public static int INVOICE_NUMBER = 0;
    public static int INVOICE_DATE = 1;

    public static int CLIENT_NAME = 2;
    public static int CLIENT_ADDRESS = 3;
    public static int CLIENT_CONTACT = 4;

    public static int SERVICE = 5;
    public static int PAYMENTS = 6;

    public static int AMOUNT_NO_TAXES = 7;
    public static int QUANTITY = 8;
    public static int VAT = 9;
    public static int DISCOUNT = 10;

    public static int ENTERPRISE_NAME = 11;
    public static int ENTERPRISE_SIREN = 12;
    public static int ENTERPRISE_ADDRESS = 13;
    public static int ENTERPRISE_CONTACT = 14;
    public static int ENTERPRISE_VAT_MATRICULATION = 15;
}
