package com.graphy.lms.service.inventory;

import com.graphy.lms.entity.AssetsAssigned;
import java.util.List;

/**
 * Service interface for Asset Assignment management.
 * Supports full CRUD operations for faculty asset tracking.
 */
public interface AssetService {

    // 1. CREATE
    AssetsAssigned assignAssetToFaculty(AssetsAssigned assignment);

    // 2. READ
    List<AssetsAssigned> getFacultyAssets(Long facultyId);

    // 3. UPDATE - Supports the "Fetch-then-Update" pattern
    AssetsAssigned updateAssignment(Long id, AssetsAssigned details);

    // 4. DELETE - Supports the "Fetch-then-Delete" pattern
    void deleteAssignment(Long id);
}