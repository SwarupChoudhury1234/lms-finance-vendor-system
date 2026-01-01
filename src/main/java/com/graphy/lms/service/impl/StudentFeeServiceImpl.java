package com.graphy.lms.service.impl;

import com.graphy.lms.entity.StudentFeeAllocation;
import com.graphy.lms.repository.StudentFeeAllocationRepository;
import com.graphy.lms.service.StudentFeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class StudentFeeServiceImpl implements StudentFeeService {

    @Autowired
    private StudentFeeAllocationRepository allocationRepository;

    @Override
    @Transactional
    public StudentFeeAllocation saveAllocation(StudentFeeAllocation allocation) {
        return allocationRepository.save(allocation);
    }

    @Override
    public List<StudentFeeAllocation> getAllAllocations() {
        return allocationRepository.findAll();
    }

    @Override
    public StudentFeeAllocation getAllocationById(Long id) {
        return allocationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Allocation ID " + id + " not found."));
    }

    @Override
    @Transactional
    public StudentFeeAllocation updateAllocation(Long id, StudentFeeAllocation details) {
        // Mentor Requirement: Fetch-then-Update pattern
        StudentFeeAllocation existing = allocationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Update Failed: ID " + id + " not found."));

        // Syncing all fields defined in the Entity
        existing.setDueDate(details.getDueDate());
        existing.setStatus(details.getStatus());
        existing.setAmountPaid(details.getAmountPaid());
        existing.setUserId(details.getUserId());
        
        return allocationRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteAllocation(Long id) {
        if (!allocationRepository.existsById(id)) {
            throw new RuntimeException("Delete Failed: ID " + id + " not found.");
        }
        allocationRepository.deleteById(id);
    }
}