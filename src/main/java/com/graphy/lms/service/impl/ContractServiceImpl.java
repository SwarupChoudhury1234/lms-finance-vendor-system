package com.graphy.lms.service.impl;

import com.graphy.lms.entity.VendorContract;
import com.graphy.lms.repository.VendorContractRepository;
import com.graphy.lms.repository.VendorRepository; // Added to verify vendor exists
import com.graphy.lms.service.vendor.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ContractServiceImpl implements ContractService {

    @Autowired
    private VendorContractRepository contractRepository;

    @Autowired
    private VendorRepository vendorRepository; // Required to check parent vendor

    // 1. CREATE with Verification
    @Override
    @Transactional
    public VendorContract addContract(VendorContract contract) {
        // Step A: Check if the Vendor linked to this contract actually exists
        if (contract.getVendor() == null || contract.getVendor().getId() == null) {
            throw new RuntimeException("Validation Failed: Vendor ID must be provided.");
        }
        
        Long vId = contract.getVendor().getId();
        vendorRepository.findById(vId)
            .orElseThrow(() -> new RuntimeException("Foreign Key Error: Vendor with ID " + vId + " does not exist."));

        // Step B: If vendor exists, save the contract
        return contractRepository.save(contract);
    }

    // 2. READ
    @Override
    public List<VendorContract> getAllContracts() {
        return contractRepository.findAll();
    }

    // 3. UPDATE (Mentor's Requirement: Fetch-then-Update)
    @Override
    @Transactional
    public VendorContract updateContract(Long id, VendorContract details) {
        // Fetch existing contract
        VendorContract existing = contractRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Update Failed: Contract ID " + id + " not found."));

        // Update fields
        existing.setContractDetails(details.getContractDetails());
        existing.setStartDate(details.getStartDate());
        existing.setEndDate(details.getEndDate());

        return contractRepository.save(existing);
    }
 // 4. DELETE (Mentor's Requirement: Fetch-then-Delete with Exception Handling)
    @Override
    @Transactional
    public void deleteContract(Long id) {
        // Step A: Fetch first to verify it exists (Exception handling)
        VendorContract contract = contractRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Delete Failed: Contract ID " + id + " not found in database."));
        
        // Step B: Delete from DB
        contractRepository.delete(contract);
    }
}