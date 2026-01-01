package com.graphy.lms.controller;

import com.graphy.lms.entity.VendorContract;
import com.graphy.lms.service.vendor.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/vendor-contracts")
public class VendorContractController {

    @Autowired
    private ContractService contractService;

    @PostMapping("/add")
    public VendorContract createContract(@RequestBody VendorContract contract) {
        return contractService.addContract(contract);
    }

    @GetMapping("/all")
    public List<VendorContract> listContracts() {
        return contractService.getAllContracts();
    }

    // --- ADD THESE METHODS BELOW ---

    // UPDATE End point
    @PutMapping("/update/{id}")
    public VendorContract updateContract(@PathVariable Long id, @RequestBody VendorContract contract) {
        // Calls the "Fetch-then-Update" logic from your Service
        return contractService.updateContract(id, contract);
    }

    // DELETE End point
    @DeleteMapping("/delete/{id}")
    public String deleteContract(@PathVariable Long id) {
        // Calls the "Fetch-then-Delete" logic from your Service
        contractService.deleteContract(id);
        return "Contract with ID " + id + " deleted successfully.";
    }
}