package com.graphy.lms.controller;

import com.graphy.lms.entity.StockTransaction;
import com.graphy.lms.service.inventory.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    // 1. CREATE
    @PostMapping("/add")
    public StockTransaction addManualTransaction(@RequestBody StockTransaction tx) {
        return transactionService.createManualTransaction(tx);
    }

    // 2. READ
    @GetMapping("/history/{itemId}")
    public List<StockTransaction> getHistory(@PathVariable Long itemId) {
        return transactionService.getHistoryByItem(itemId);
    }

    // 3. UPDATE
    @PutMapping("/update/{id}")
    public StockTransaction updateTx(@PathVariable Long id, @RequestBody StockTransaction details) {
        return transactionService.updateTransaction(id, details);
    }

    // 4. DELETE
    @DeleteMapping("/delete/{id}")
    public String deleteTx(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return "Transaction record " + id + " deleted successfully.";
    }
}