package com.graphy.lms.service.impl;

import com.graphy.lms.entity.Vendor;
import com.graphy.lms.repository.VendorRepository;
import com.graphy.lms.service.vendor.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class VendorServiceImpl implements VendorService {

    @Autowired
    private VendorRepository vendorRepository;

    @Override
    @Transactional
    public Vendor addVendor(Vendor vendor) {
        return vendorRepository.save(vendor);
    }

    @Override
    public List<Vendor> getAllVendors() {
        return vendorRepository.findAll();
    }

    @Override
    public Vendor getVendorById(Long id) {
        return vendorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CRITICAL: Vendor with ID " + id + " not found."));
    }

    @Override
    @Transactional
    public Vendor updateVendor(Long id, Vendor details) {
        // Step A: Fetch (Mentor's Requirement)
        Vendor existingVendor = getVendorById(id);
        
        // Step B: Update fields with Null-Checks (Safer for Postman)
        if(details.getVendorName() != null) existingVendor.setVendorName(details.getVendorName());
        if(details.getContactPerson() != null) existingVendor.setContactPerson(details.getContactPerson());
        if(details.getEmail() != null) existingVendor.setEmail(details.getEmail());
        if(details.getPhone() != null) existingVendor.setPhone(details.getPhone());
        
        // Step C: Save (Persist the Fetch-then-Update result)
        return vendorRepository.save(existingVendor);
    }

    @Override
    @Transactional
    public void deleteVendor(Long id) {
        Vendor vendor = getVendorById(id);
        vendorRepository.delete(vendor);
    }
}