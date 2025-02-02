## Open facture pdf

API rest Java spring boot for generating bulk PDF invoices for french companies (this could be adapted). This API provides an endpoint only, taking a list of invoices as input and returning a zip file containing an associated PDF for each invoice.

### Technical specifications

- Spring Boot 3.3.4
- Java 21
- Apache pdfbox 3.0.3


### Rest API contract

| method | url     |
|--------|---------|
 | POST   | /import |

#### Request body

```json
{
  "invoices": [
    {
      "invoiceNumber": "string",
      "invoiceDate": "string",
      "clientName": "string",
      "clientAddress" : "string",
      "clientContact": "string",
      "service": "string",
      "payments": "string",
      "amountNoTaxes": 0.0,
      "quantity": 0,
      "vat": 0.0,
      "discount": 0.0,
      "amountOfVat": 0.0,
      "totalAmountNoTaxes": 0.0,
      "totalAmountWithTaxes": 0.0,
      "enterprise": {
        "name": "string",
        "siren": "string",
        "address": "string",
        "vatMatriculation": "string",
        "contact": "string"
      }
    }
  ]
}
```

#### Response

**invoices.zip** : Zip archive which contains all the PDF files