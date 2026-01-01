package com.graphy.lms.controller;

import com.graphy.lms.entity.StudentFeeAllocation;
import com.graphy.lms.service.StudentFeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/student-fees")
public class StudentFeeController {

    @Autowired
    private StudentFeeService studentFeeService;

    @PostMapping("/add")
    public StudentFeeAllocation createAllocation(@RequestBody StudentFeeAllocation allocation) {
        return studentFeeService.saveAllocation(allocation);
    }

    @GetMapping("/all")
    public List<StudentFeeAllocation> getAll() {
        return studentFeeService.getAllAllocations();
    }

    @PutMapping("/update/{id}")
    public StudentFeeAllocation updateAllocation(@PathVariable Long id, @RequestBody StudentFeeAllocation details) {
        return studentFeeService.updateAllocation(id, details);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteAllocation(@PathVariable Long id) {
        studentFeeService.deleteAllocation(id);
        return "Student Fee Allocation with ID " + id + " has been deleted successfully.";
    }
}