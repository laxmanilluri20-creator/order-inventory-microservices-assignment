package com.inventory.handler;


import com.inventory.dto.InventoryUpdateRequest;


public interface InventoryHandler {
    /**
     * Apply the inventory update described by request (can be deduction after order or adjustments).
     * Implementation should be idempotent and transactional at service layer.
     */
    void handleUpdate(InventoryUpdateRequest request);
}