package com.graphy.lms.service;

import com.graphy.lms.entity.StudentFeeAllocation;
import java.util.List;

public interface StudentFeeService {

    // CREATE
    StudentFeeAllocation saveAllocation(StudentFeeAllocation allocation);

    // READ ALL
    List<StudentFeeAllocation> getAllAllocations();

    // READ BY ID
    StudentFeeAllocation getAllocationById(Long id);

    // UPDATE (Added this to match Implementation and Controller)
    StudentFeeAllocation updateAllocation(Long id, StudentFeeAllocation details);

    // DELETE
    void deleteAllocation(Long id);
}