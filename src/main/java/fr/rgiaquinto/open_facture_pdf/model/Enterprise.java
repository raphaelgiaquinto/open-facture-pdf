package fr.rgiaquinto.open_facture_pdf.model;

public class Enterprise {
    private String name;
    private String siren;
    private String address;
    private String vatMatriculation;
    private String contact;

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSiren() {
        return siren;
    }

    public void setSiren(String siren) {
        this.siren = siren;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getVatMatriculation() {
        return vatMatriculation;
    }

    public void setVatMatriculation(String vatMatriculation) {
        this.vatMatriculation = vatMatriculation;
    }
}
