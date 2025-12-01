package com.order.dto;

import com.order.model.OrderItemEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {
    @NotBlank
    private String orderId;

    @NotEmpty
    @Valid
    private List<OrderItem> items;
}
