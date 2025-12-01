package com.inventory.model;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryBatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(optional = false)
    private Product product;


    private Integer quantity;


    private LocalDate expiryDate;


    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(LocalDate.now());
    }


}