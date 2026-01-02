package com.graphy.lms.service.impl;

import com.graphy.lms.entity.*;
import com.graphy.lms.repository.*;
import com.graphy.lms.service.BillingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BillingServiceImpl implements BillingService {

    @Autowired private PurchaseOrdersRepository orderRepo;
    @Autowired private InvoicesRepository invoiceRepo;
    @Autowired private BillingTransactionsRepository transRepo;

    // --- 1. PURCHASE ORDERS ---
    @Override public PurchaseOrders saveOrder(PurchaseOrders o) { 
        o.setUpdatedAt(null); // Ensure null on creation
        return orderRepo.save(o); 
    }
    @Override public PurchaseOrders getOrderById(Long id) { 
        return orderRepo.findById(id).orElseThrow(() -> new RuntimeException("Order not found: " + id)); 
    }
    @Override public List<PurchaseOrders> getAllOrders() { return orderRepo.findAll(); }
    @Override public PurchaseOrders updateOrder(Long id, PurchaseOrders o) {
        PurchaseOrders ex = getOrderById(id);
        ex.setVendorId(o.getVendorId());
        ex.setOrderDate(o.getOrderDate());
        ex.setSubtotalAmount(o.getSubtotalAmount());
        ex.setTaxAmount(o.getTaxAmount());
        ex.setTotalAmount(o.getTotalAmount());
        ex.setStatus(o.getStatus());
        ex.setUpdatedAt(LocalDateTime.now()); // Set current time on update
        return orderRepo.save(ex);
    }
    @Override public void deleteOrder(Long id) { orderRepo.deleteById(id); }

    // --- 2. INVOICES ---
    @Override public Invoices saveInvoice(Invoices i) { 
        i.setUpdatedAt(null); // Ensure null on creation
        return invoiceRepo.save(i); 
    }
    @Override public Invoices getInvoiceById(Long id) { 
        return invoiceRepo.findById(id).orElseThrow(() -> new RuntimeException("Invoice not found: " + id)); 
    }
    @Override public List<Invoices> getAllInvoices() { return invoiceRepo.findAll(); }
    @Override public Invoices updateInvoice(Long id, Invoices i) {
        Invoices ex = getInvoiceById(id);
        ex.setPurchaseOrder(i.getPurchaseOrder());
        ex.setInvoiceNumber(i.getInvoiceNumber());
        ex.setBillDate(i.getBillDate());
        ex.setDueDate(i.getDueDate());
        ex.setInvoiceAmount(i.getInvoiceAmount());
        ex.setAmountPaid(i.getAmountPaid());
        ex.setBalanceAmount(i.getBalanceAmount());
        ex.setStatus(i.getStatus());
        ex.setCurrency(i.getCurrency());
        ex.setUpdatedAt(LocalDateTime.now()); // Set current time on update
        return invoiceRepo.save(ex);
    }
    @Override public void deleteInvoice(Long id) { invoiceRepo.deleteById(id); }

    // --- 3. BILLING TRANSACTIONS ---
    @Override public BillingTransactions saveTransaction(BillingTransactions t) { 
        t.setUpdatedAt(null); // Ensure null on creation
        return transRepo.save(t); 
    }
    @Override public BillingTransactions getTransactionById(Long id) { 
        return transRepo.findById(id).orElseThrow(() -> new RuntimeException("Transaction not found: " + id)); 
    }
    @Override public List<BillingTransactions> getAllTransactions() { return transRepo.findAll(); }
    @Override public BillingTransactions updateTransaction(Long id, BillingTransactions t) {
        BillingTransactions ex = getTransactionById(id);
        ex.setInvoice(t.getInvoice());
        ex.setTransactionDate(t.getTransactionDate());
        ex.setAmount(t.getAmount());
        ex.setPaymentMode(t.getPaymentMode());
        ex.setTransactionReference(t.getTransactionReference());
        ex.setStatus(t.getStatus());
        ex.setUpdatedAt(LocalDateTime.now()); // Set current time on update
        return transRepo.save(ex);
    }
    @Override public void deleteTransaction(Long id) { transRepo.deleteById(id); }
}