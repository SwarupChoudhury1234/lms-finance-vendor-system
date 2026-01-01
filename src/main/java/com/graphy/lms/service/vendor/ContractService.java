package com.graphy.lms.service.vendor;

import com.graphy.lms.entity.VendorContract;
import java.util.List;

/**
 * Service interface for Vendor Contract management.
 * Follows full CRUD pattern as per mentor's requirements.
 */
public interface ContractService {

    // 1. CREATE
    VendorContract addContract(VendorContract contract);

    // 2. READ
    List<VendorContract> getAllContracts();

    // 3. UPDATE (Required for your Impl class to work)
    VendorContract updateContract(Long id, VendorContract details);

    // 4. DELETE (Required for full CRUD interface)
    void deleteContract(Long id);
}