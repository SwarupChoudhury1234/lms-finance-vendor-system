package com.graphy.lms.controller;

import com.graphy.lms.entity.*;
import com.graphy.lms.service.BillingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/billing")
public class BillingController {

    @Autowired private BillingService service;

    
    // 1. PURCHASE ORDERS
    @PostMapping("/orders") public PurchaseOrders addO(@RequestBody PurchaseOrders o) { return service.saveOrder(o); }
    @GetMapping("/orders/{id}") public PurchaseOrders getO(@PathVariable Long id) { return service.getOrderById(id); }
    @GetMapping("/orders") public List<PurchaseOrders> getAllO() { return service.getAllOrders(); }
    @PutMapping("/orders/{id}") public PurchaseOrders updateO(@PathVariable Long id, @RequestBody PurchaseOrders o) { return service.updateOrder(id, o); }
    @DeleteMapping("/orders/{id}") public String delO(@PathVariable Long id) { service.deleteOrder(id); return "Order Deleted"; }

    // 2. INVOICES
    @PostMapping("/invoices") public Invoices addI(@RequestBody Invoices i) { return service.saveInvoice(i); }
    @GetMapping("/invoices/{id}") public Invoices getI(@PathVariable Long id) { return service.getInvoiceById(id); }
    @GetMapping("/invoices") public List<Invoices> getAllI() { return service.getAllInvoices(); }
    @PutMapping("/invoices/{id}") public Invoices updateI(@PathVariable Long id, @RequestBody Invoices i) { return service.updateInvoice(id, i); }
    @DeleteMapping("/invoices/{id}") public String delI(@PathVariable Long id) { service.deleteInvoice(id); return "Invoice Deleted"; }

    // 3. BILLING TRANSACTIONS
    @PostMapping("/transactions") public BillingTransactions addT(@RequestBody BillingTransactions t) { return service.saveTransaction(t); }
    @GetMapping("/transactions/{id}") public BillingTransactions getT(@PathVariable Long id) { return service.getTransactionById(id); }
    @GetMapping("/transactions") public List<BillingTransactions> getAllT() { return service.getAllTransactions(); }
    @PutMapping("/transactions/{id}") public BillingTransactions updateT(@PathVariable Long id, @RequestBody BillingTransactions t) { return service.updateTransaction(id, t); }
    @DeleteMapping("/transactions/{id}") public String delT(@PathVariable Long id) { service.deleteTransaction(id); return "Transaction Deleted"; }
}