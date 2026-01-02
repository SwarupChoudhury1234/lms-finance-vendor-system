package com.graphy.lms.service.impl;

import com.graphy.lms.entity.*;
import com.graphy.lms.repository.*;
import com.graphy.lms.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class VendorServiceImpl implements VendorService {

    @Autowired private VendorsRepository vendorRepo;
    @Autowired private VendorContractsRepository contractRepo;
    @Autowired private VendorServicesRepository serviceRepo;
    @Autowired private VendorPaymentsRepository paymentRepo;
    @Autowired private VendorTransactionsRepository transRepo;

    // --- 1. VENDORS ---
    @Override public Vendors saveVendor(Vendors v) { return vendorRepo.save(v); }
    @Override public Vendors getVendorById(Long id) { return vendorRepo.findById(id).orElseThrow(() -> new RuntimeException("Vendor not found: " + id)); }
    @Override public List<Vendors> getAllVendors() { return vendorRepo.findAll(); }
    @Override public Vendors updateVendor(Long id, Vendors v) {
        Vendors ex = getVendorById(id);
        ex.setVendorName(v.getVendorName()); ex.setVendorType(v.getVendorType());
        ex.setContactPerson(v.getContactPerson()); ex.setEmail(v.getEmail());
        ex.setPhone(v.getPhone()); ex.setAddress(v.getAddress());
        ex.setStatus(v.getStatus());
        return vendorRepo.save(ex);
    }
    @Override public void deleteVendor(Long id) { vendorRepo.deleteById(id); }

    // --- 2. CONTRACTS ---
    @Override public VendorContracts saveContract(VendorContracts c) { return contractRepo.save(c); }
    @Override public VendorContracts getContractById(Long id) { return contractRepo.findById(id).orElseThrow(() -> new RuntimeException("Contract not found: " + id)); }
    @Override public List<VendorContracts> getAllContracts() { return contractRepo.findAll(); }
    @Override public VendorContracts updateContract(Long id, VendorContracts c) {
        VendorContracts ex = getContractById(id);
        ex.setVendor(c.getVendor()); ex.setContractTitle(c.getContractTitle());
        ex.setStartDate(c.getStartDate()); ex.setEndDate(c.getEndDate());
        ex.setContractStatus(c.getContractStatus());
        return contractRepo.save(ex);
    }
    @Override public void deleteContract(Long id) { contractRepo.deleteById(id); }

    // --- 3. SERVICES ---
    @Override public VendorServices saveService(VendorServices s) { return serviceRepo.save(s); }
    @Override public VendorServices getServiceById(Long id) { return serviceRepo.findById(id).orElseThrow(() -> new RuntimeException("Service not found: " + id)); }
    @Override public List<VendorServices> getAllServices() { return serviceRepo.findAll(); }
    @Override public VendorServices updateService(Long id, VendorServices s) {
        VendorServices ex = getServiceById(id);
        ex.setVendor(s.getVendor()); ex.setServiceName(s.getServiceName());
        ex.setServiceDescription(s.getServiceDescription()); ex.setStatus(s.getStatus());
        return serviceRepo.save(ex);
    }
    @Override public void deleteService(Long id) { serviceRepo.deleteById(id); }

    // --- 4. PAYMENTS ---
    @Override public VendorPayments savePayment(VendorPayments p) { return paymentRepo.save(p); }
    @Override public VendorPayments getPaymentById(Long id) { return paymentRepo.findById(id).orElseThrow(() -> new RuntimeException("Payment not found: " + id)); }
    @Override public List<VendorPayments> getAllPayments() { return paymentRepo.findAll(); }
    @Override public VendorPayments updatePayment(Long id, VendorPayments p) {
        VendorPayments ex = getPaymentById(id);
        ex.setVendor(p.getVendor()); ex.setContract(p.getContract());
        ex.setPaymentAmount(p.getPaymentAmount()); ex.setPaymentDate(p.getPaymentDate());
        ex.setPaymentMode(p.getPaymentMode()); ex.setPaymentStatus(p.getPaymentStatus());
        ex.setReferenceNumber(p.getReferenceNumber());
        return paymentRepo.save(ex);
    }
    @Override public void deletePayment(Long id) { paymentRepo.deleteById(id); }

    // --- 5. TRANSACTIONS ---
    @Override public VendorTransactions saveTransaction(VendorTransactions t) { return transRepo.save(t); }
    @Override public VendorTransactions getTransactionById(Long id) { return transRepo.findById(id).orElseThrow(() -> new RuntimeException("Transaction not found: " + id)); }
    @Override public List<VendorTransactions> getAllTransactions() { return transRepo.findAll(); }
    @Override public VendorTransactions updateTransaction(Long id, VendorTransactions t) {
        VendorTransactions ex = getTransactionById(id);
        ex.setVendor(t.getVendor()); ex.setTransactionType(t.getTransactionType());
        ex.setReferenceId(t.getReferenceId()); ex.setRemarks(t.getRemarks());
        ex.setTransactionDate(t.getTransactionDate());
        return transRepo.save(ex);
    }
    @Override public void deleteTransaction(Long id) { transRepo.deleteById(id); }
}