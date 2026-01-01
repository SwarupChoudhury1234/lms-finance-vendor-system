package com.graphy.lms.service.impl;

import com.graphy.lms.entity.InventoryItem;
import com.graphy.lms.repository.InventoryItemRepository;
import com.graphy.lms.service.inventory.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private InventoryItemRepository itemRepository;

    @Override
    public InventoryItem addItem(InventoryItem item) {
        return itemRepository.save(item);
    }

    @Override
    public List<InventoryItem> getAllItems() {
        return itemRepository.findAll();
    }

    @Override
    public InventoryItem getItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory Item not found with ID: " + id));
    }

    @Override
    @Transactional
    public InventoryItem updateItem(Long id, InventoryItem details) {
        // Step A: Fetch (Mentor's Pattern)
        InventoryItem existingItem = getItemById(id);

        // Step B: Update fields
        existingItem.setItemName(details.getItemName());
        existingItem.setCategory(details.getCategory());
        existingItem.setUnitPrice(details.getUnitPrice());
        existingItem.setTotalQuantity(details.getTotalQuantity());

        return itemRepository.save(existingItem);
    }

    @Override
    @Transactional
    public void deleteItem(Long id) {
        InventoryItem existingItem = getItemById(id);
        itemRepository.delete(existingItem);
    }
}