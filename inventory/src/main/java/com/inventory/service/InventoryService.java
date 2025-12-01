package com.inventory.service;


import com.inventory.dto.InventoryBatchDto;
import com.inventory.dto.InventoryUpdateRequest;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public interface InventoryService {
    /**
     * Returns batches for product SKU sorted by expiry date ascending.
     */
    List<InventoryBatchDto> getBatchesByProductSku(String sku);

    /**
     * Applies an inventory update (deduction or adjustment).
     */
    void applyUpdate(InventoryUpdateRequest request);
}
