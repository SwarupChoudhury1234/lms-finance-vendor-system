package com.graphy.lms.service.impl;

import com.graphy.lms.entity.Invoice;
import com.graphy.lms.repository.InvoiceRepository;
import com.graphy.lms.service.billing.BillingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class BillingServiceImpl implements BillingService {

    @Autowired
    private InvoiceRepository invoiceRepo;

    @Override
    public Invoice createInvoice(Invoice invoice) {
        return invoiceRepo.save(invoice);
    }

    @Override
    public List<Invoice> getAllInvoices() {
        return invoiceRepo.findAll();
    }

    @Override
    public Invoice getInvoiceById(Long id) {
        return invoiceRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found with ID: " + id));
    }

    @Override
    @Transactional
    public Invoice updateInvoice(Long id, Invoice details) {
        // Step A: Fetch (Mentor's Pattern)
        Invoice existing = getInvoiceById(id);

        // Step B: Update and Log Balance Change
        Double oldBalance = existing.getTotalAmount();
        existing.setStatus(details.getStatus());
        existing.setTotalAmount(details.getTotalAmount());
        
        // Note: You can call a Transaction logger here to save oldBalance and newBalance
        
        return invoiceRepo.save(existing);
    }

    @Override
    @Transactional
    public void deleteInvoice(Long id) {
        Invoice existing = getInvoiceById(id);
        invoiceRepo.delete(existing);
    }
}