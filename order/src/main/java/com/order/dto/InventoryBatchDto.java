package com.order.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryBatchDto {
    private Long id;
    private String sku;
    private Integer quantity;
    private LocalDate expiryDate;
}
