package com.inventory.dto;
import lombok.*;


import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryUpdateRequest
{
    private String sku;
    private List<BatchUpdate> batches;

    public String getSku() {
        return sku;
    }

    public List<BatchUpdate> getBatches() {
        return batches;
    }


    @Getter
    @Setter
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BatchUpdate {
        private Long batchId; // optional: update specific batch
        private Integer quantityChange; // negative for deduction

    }
}
