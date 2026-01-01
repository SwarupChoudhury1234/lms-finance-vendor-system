package com.graphy.lms.controller;

import com.graphy.lms.entity.AssetsAssigned;
import com.graphy.lms.service.inventory.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/faculty-assets")
public class AssetController {

    @Autowired
    private AssetService assetService;

    // 1. CREATE - Faculty member takes an item (e.g., Laptop)
    @PostMapping("/assign")
    public AssetsAssigned assignAsset(
            @RequestBody AssetsAssigned assignment, 
            @RequestHeader("user-id") Long facultyIdFromToken) { 
        
        // Force the ID from the token into the assignment object
        assignment.setUserId(facultyIdFromToken); 
        return assetService.assignAssetToFaculty(assignment);
    }

    // 2. READ - Faculty member sees all items they currently have
    @GetMapping("/my-list")
    public List<AssetsAssigned> getMyAssets(@RequestHeader("user-id") Long facultyIdFromToken) {
        return assetService.getFacultyAssets(facultyIdFromToken);
    }

    // 3. UPDATE - Fetch-then-Update pattern
    // URL: http://localhost:8080/api/faculty-assets/update/1
    @PutMapping("/update/{id}")
    public AssetsAssigned updateAssignment(
            @PathVariable Long id, 
            @RequestBody AssetsAssigned details) {
        return assetService.updateAssignment(id, details);
    }

    // 4. DELETE - Fetch-then-Delete pattern
    // URL: http://localhost:8080/api/faculty-assets/delete/1
    @DeleteMapping("/delete/{id}")
    public String deleteAssignment(@PathVariable Long id) {
        assetService.deleteAssignment(id);
        return "Asset assignment record " + id + " deleted successfully.";
    }
}