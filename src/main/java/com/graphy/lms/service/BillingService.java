package com.graphy.lms.service;

import com.graphy.lms.entity.*;
import java.util.List;

public interface BillingService {
    // 1. Purchase Orders
    PurchaseOrders saveOrder(PurchaseOrders o);
    PurchaseOrders getOrderById(Long id);
    List<PurchaseOrders> getAllOrders();
    PurchaseOrders updateOrder(Long id, PurchaseOrders o);
    void deleteOrder(Long id);

    // 2. Invoices
    Invoices saveInvoice(Invoices i);
    Invoices getInvoiceById(Long id);
    List<Invoices> getAllInvoices();
    Invoices updateInvoice(Long id, Invoices i);
    void deleteInvoice(Long id);

    // 3. Billing Transactions
    BillingTransactions saveTransaction(BillingTransactions t);
    BillingTransactions getTransactionById(Long id);
    List<BillingTransactions> getAllTransactions();
    BillingTransactions updateTransaction(Long id, BillingTransactions t);
    void deleteTransaction(Long id);
}