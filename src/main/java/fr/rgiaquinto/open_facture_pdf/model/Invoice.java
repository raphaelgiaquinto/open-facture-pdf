package fr.rgiaquinto.open_facture_pdf.model;

public class Invoice {

    private Enterprise enterprise;
    private String invoiceNumber;
    private String invoiceDate;
    private String clientName;

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public String getClientContact() {
        return clientContact;
    }

    public void setClientContact(String clientContact) {
        this.clientContact = clientContact;
    }

    private String clientAddress;
    private String clientContact;
    private String service;
    private String payments;
    private Double amountNoTaxes;
    private Integer quantity;
    private Double vat;
    private Double discount;
    private Double amountOfVAT;
    private Double totalAmountNoTaxes;
    private Double totalAmountWithTaxes;

    public Double getAmountOfVAT() {
        return amountOfVAT;
    }

    public void setAmountOfVAT(Double amountOfVAT) {
        this.amountOfVAT = amountOfVAT;
    }


    public Double getTotalAmountNoTaxes() {
        return totalAmountNoTaxes;
    }

    public void setTotalAmountNoTaxes(Double totalAmountNoTaxes) {
        this.totalAmountNoTaxes = totalAmountNoTaxes;
    }

    public Enterprise getEnterprise() {
        return this.enterprise;
    }

    public void setEnterprise(Enterprise enterprise) {
        this.enterprise = enterprise;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getPayments() {
        return payments;
    }

    public void setPayments(String payments) {
        this.payments = payments;
    }

    public Double getAmountNoTaxes() {
        return amountNoTaxes;
    }

    public void setAmountNoTaxes(Double amountNoTaxes) {
        this.amountNoTaxes = amountNoTaxes;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getVat() {
        return vat;
    }

    public void setVat(Double vat) {
        this.vat = vat;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Double getTotalAmountWithTaxes() {
        return totalAmountWithTaxes;
    }

    public void setTotalAmountWithTaxes(Double totalAmountWithTaxes) {
        this.totalAmountWithTaxes = totalAmountWithTaxes;
    }

    public void computeAmountWithoutTaxes() {
        this.totalAmountNoTaxes = this.amountNoTaxes * this.quantity;
    }

    public void computeAmountOfVAT() {
        if (this.vat == null || this.vat <= 0) {
            this.amountOfVAT = 0D;
        } else {
            this.amountOfVAT = this.totalAmountNoTaxes * ( this.vat / 100 );
        }
    }

    public void computeAmountWithTaxes() {
        this.computeAmountWithoutTaxes();
        this.computeAmountOfVAT();
        this.setTotalAmountWithTaxes(this.totalAmountNoTaxes + this.amountOfVAT - this.discount);
    }
}
