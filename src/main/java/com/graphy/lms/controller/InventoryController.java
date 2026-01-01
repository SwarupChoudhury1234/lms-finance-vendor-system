package com.graphy.lms.controller;

import com.graphy.lms.entity.InventoryItem;
import com.graphy.lms.service.inventory.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryService itemService;

    @PostMapping
    public InventoryItem createItem(@RequestBody InventoryItem item) {
        return itemService.addItem(item);
    }

    @GetMapping
    public List<InventoryItem> listAll() {
        return itemService.getAllItems();
    }

    @GetMapping("/{id}")
    public InventoryItem getById(@PathVariable Long id) {
        return itemService.getItemById(id);
    }

    // UPDATE (PUT)
    @PutMapping("/{id}")
    public InventoryItem update(@PathVariable Long id, @RequestBody InventoryItem details) {
        return itemService.updateItem(id, details);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        itemService.deleteItem(id);
        return "Item " + id + " deleted successfully.";
    }
}