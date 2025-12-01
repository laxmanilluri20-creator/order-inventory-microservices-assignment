package com.inventory.controller;

import com.inventory.dto.InventoryBatchDto;
import com.inventory.dto.InventoryUpdateRequest;
import com.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController
{

    @Autowired
    private InventoryService inventoryService;

    /**
     * GET /inventory/{sku} - returns list of batches sorted by expiry date (ascending).
     */
    @GetMapping("/{sku}")
    public ResponseEntity<List<InventoryBatchDto>> getBatches(@PathVariable("sku") String sku) {
        List<InventoryBatchDto> batches = inventoryService.getBatchesByProductSku(sku);
        return ResponseEntity.ok(batches);
    }

    /**
     * POST /inventory/update - apply inventory changes (deduct after order, adjustments, etc.)
     */
    @PostMapping("/update")
    public ResponseEntity<Void> updateInventory(@RequestBody InventoryUpdateRequest request) {
        inventoryService.applyUpdate(request);
        return ResponseEntity.ok().build();
    }
}
