package com.graphy.lms.controller;

import com.graphy.lms.entity.*;
import com.graphy.lms.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/vendor")
public class VendorController {

    @Autowired private VendorService service;

    // 1. VENDORS
    @PostMapping("/vendors") public Vendors addV(@RequestBody Vendors v) { return service.saveVendor(v); }
    @GetMapping("/vendors/{id}") public Vendors getV(@PathVariable Long id) { return service.getVendorById(id); }
    @GetMapping("/vendors") public List<Vendors> getAllV() { return service.getAllVendors(); }
    @PutMapping("/vendors/{id}") public Vendors updateV(@PathVariable Long id, @RequestBody Vendors v) { return service.updateVendor(id, v); }
    @DeleteMapping("/vendors/{id}") public String delV(@PathVariable Long id) { service.deleteVendor(id); return "Vendor Deleted"; }

    // 2. CONTRACTS
    @PostMapping("/contracts") public VendorContracts addC(@RequestBody VendorContracts c) { return service.saveContract(c); }
    @GetMapping("/contracts/{id}") public VendorContracts getC(@PathVariable Long id) { return service.getContractById(id); }
    @GetMapping("/contracts") public List<VendorContracts> getAllC() { return service.getAllContracts(); }
    @PutMapping("/contracts/{id}") public VendorContracts updateC(@PathVariable Long id, @RequestBody VendorContracts c) { return service.updateContract(id, c); }
    @DeleteMapping("/contracts/{id}") public String delC(@PathVariable Long id) { service.deleteContract(id); return "Contract Deleted"; }

    // 3. SERVICES
    @PostMapping("/services") public VendorServices addS(@RequestBody VendorServices s) { return service.saveService(s); }
    @GetMapping("/services/{id}") public VendorServices getS(@PathVariable Long id) { return service.getServiceById(id); }
    @GetMapping("/services") public List<VendorServices> getAllS() { return service.getAllServices(); }
    @PutMapping("/services/{id}") public VendorServices updateS(@PathVariable Long id, @RequestBody VendorServices s) { return service.updateService(id, s); }
    @DeleteMapping("/services/{id}") public String delS(@PathVariable Long id) { service.deleteService(id); return "Service Deleted"; }

    // 4. PAYMENTS
    @PostMapping("/payments") public VendorPayments addP(@RequestBody VendorPayments p) { return service.savePayment(p); }
    @GetMapping("/payments/{id}") public VendorPayments getP(@PathVariable Long id) { return service.getPaymentById(id); }
    @GetMapping("/payments") public List<VendorPayments> getAllP() { return service.getAllPayments(); }
    @PutMapping("/payments/{id}") public VendorPayments updateP(@PathVariable Long id, @RequestBody VendorPayments p) { return service.updatePayment(id, p); }
    @DeleteMapping("/payments/{id}") public String delP(@PathVariable Long id) { service.deletePayment(id); return "Payment Deleted"; }

    // 5. TRANSACTIONS
    @PostMapping("/transactions") public VendorTransactions addT(@RequestBody VendorTransactions t) { return service.saveTransaction(t); }
    @GetMapping("/transactions/{id}") public VendorTransactions getT(@PathVariable Long id) { return service.getTransactionById(id); }
    @GetMapping("/transactions") public List<VendorTransactions> getAllT() { return service.getAllTransactions(); }
    @PutMapping("/transactions/{id}") public VendorTransactions updateT(@PathVariable Long id, @RequestBody VendorTransactions t) { return service.updateTransaction(id, t); }
    @DeleteMapping("/transactions/{id}") public String delT(@PathVariable Long id) { service.deleteTransaction(id); return "Transaction Deleted"; }
}