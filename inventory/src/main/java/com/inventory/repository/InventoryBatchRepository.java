package com.inventory.repository;

import com.inventory.model.InventoryBatch;
import com.inventory.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryBatchRepository extends JpaRepository<InventoryBatch, Long> {

    List<InventoryBatch> findByProductOrderByExpiryDateAsc(Product product);

}
