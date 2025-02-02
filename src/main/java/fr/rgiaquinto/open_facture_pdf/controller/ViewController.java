package fr.rgiaquinto.open_facture_pdf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class ViewController {

    @GetMapping("/index")
    public String indexView() {
        return "index";
    }

    @GetMapping("/invoices")
    public String invoicesView() {
        return "invoices";
    }

    @GetMapping("/new-invoice")
    public String newInvoiceView() {
        return "new-invoice";
    }
}
