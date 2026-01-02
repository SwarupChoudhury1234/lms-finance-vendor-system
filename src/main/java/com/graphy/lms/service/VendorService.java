package com.graphy.lms.service;

import com.graphy.lms.entity.*;
import java.util.List;

public interface VendorService {
    // 1. Vendors
    Vendors saveVendor(Vendors v);
    Vendors getVendorById(Long id);
    List<Vendors> getAllVendors();
    Vendors updateVendor(Long id, Vendors v);
    void deleteVendor(Long id);

    // 2. Vendor Contracts
    VendorContracts saveContract(VendorContracts c);
    VendorContracts getContractById(Long id);
    List<VendorContracts> getAllContracts();
    VendorContracts updateContract(Long id, VendorContracts c);
    void deleteContract(Long id);

    // 3. Vendor Services
    VendorServices saveService(VendorServices s);
    VendorServices getServiceById(Long id);
    List<VendorServices> getAllServices();
    VendorServices updateService(Long id, VendorServices s);
    void deleteService(Long id);

    // 4. Vendor Payments
    VendorPayments savePayment(VendorPayments p);
    VendorPayments getPaymentById(Long id);
    List<VendorPayments> getAllPayments();
    VendorPayments updatePayment(Long id, VendorPayments p);
    void deletePayment(Long id);

    // 5. Vendor Transactions
    VendorTransactions saveTransaction(VendorTransactions t);
    VendorTransactions getTransactionById(Long id);
    List<VendorTransactions> getAllTransactions();
    VendorTransactions updateTransaction(Long id, VendorTransactions t);
    void deleteTransaction(Long id);
}