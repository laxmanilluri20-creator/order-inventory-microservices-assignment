package com.order.dto;


import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryUpdateRequest {
    private String sku;
    private List<BatchUpdate> batches;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BatchUpdate {
        private Long batchId;          // null -> ask inventory to choose batches (FEFO)
        private Integer quantityChange; // negative for deduction
    }
}

