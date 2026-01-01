package com.graphy.lms.controller;

import com.graphy.lms.entity.Invoice;
import com.graphy.lms.service.billing.BillingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/billing")
public class BillingController {

    @Autowired
    private BillingService billingService;

    @PostMapping("/invoices")
    public Invoice generateInvoice(@RequestBody Invoice invoice) {
        return billingService.createInvoice(invoice);
    }

    @GetMapping("/invoices")
    public List<Invoice> getAll() {
        return billingService.getAllInvoices();
    }

    @PutMapping("/invoices/{id}")
    public Invoice update(@PathVariable Long id, @RequestBody Invoice details) {
        return billingService.updateInvoice(id, details);
    }

    @DeleteMapping("/invoices/{id}")
    public String delete(@PathVariable Long id) {
        billingService.deleteInvoice(id);
        return "Invoice " + id + " deleted.";
    }
}