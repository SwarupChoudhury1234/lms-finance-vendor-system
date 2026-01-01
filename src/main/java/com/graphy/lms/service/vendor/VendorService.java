package com.graphy.lms.service.vendor;

import com.graphy.lms.entity.Vendor;
import java.util.List;

/**
 * Service interface for Vendor management.
 * Provides full CRUD operations as per real-world professional requirements.
 */
public interface VendorService {

    // 1. CREATE
    Vendor addVendor(Vendor vendor);

    // 2. READ
    List<Vendor> getAllVendors();
    Vendor getVendorById(Long id);

    // 3. UPDATE (Mentor's Requirement)
    Vendor updateVendor(Long id, Vendor vendorDetails);

    // 4. DELETE (Mentor's Requirement)
    void deleteVendor(Long id);
}