package com.graphy.lms.service.billing;

import com.graphy.lms.entity.Invoice;
import java.util.List;

public interface BillingService {
    // 1. CREATE
    Invoice createInvoice(Invoice invoice);

    // 2. READ
    List<Invoice> getAllInvoices();
    Invoice getInvoiceById(Long id);

    // 3. UPDATE (Fetch-then-Update)
    Invoice updateInvoice(Long id, Invoice details);

    // 4. DELETE
    void deleteInvoice(Long id);
}