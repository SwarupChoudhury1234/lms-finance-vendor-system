package com.graphy.lms.controller;

import com.graphy.lms.entity.Vendor;
import com.graphy.lms.service.vendor.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/vendors")
public class VendorController {

    @Autowired
    private VendorService vendorService;

    // 1. CREATE
    @PostMapping
    public Vendor createVendor(@RequestBody Vendor vendor) {
        return vendorService.addVendor(vendor);
    }

    // 2. READ (All)
    @GetMapping
    public List<Vendor> listVendors() {
        return vendorService.getAllVendors();
    }

    // 2. READ (By ID)
    @GetMapping("/{id}")
    public Vendor getVendorById(@PathVariable Long id) {
        return vendorService.getVendorById(id);
    }

    // 3. UPDATE (Full Update)
    @PutMapping("/{id}")
    public Vendor updateVendor(@PathVariable Long id, @RequestBody Vendor details) {
        return vendorService.updateVendor(id, details);
    }

    // 4. DELETE
    @DeleteMapping("/{id}")
    public String deleteVendor(@PathVariable Long id) {
        vendorService.deleteVendor(id);
        return "Vendor with ID " + id + " has been successfully deleted.";
    }
}