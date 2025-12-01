package com.inventory.service;

import com.inventory.dto.InventoryBatchDto;
import com.inventory.dto.InventoryUpdateRequest;
import com.inventory.handler.InventoryHandler;
import com.inventory.handler.InventoryHandlerFactory;
import com.inventory.model.InventoryBatch;
import com.inventory.model.Product;
import com.inventory.repository.InventoryBatchRepository;
import com.inventory.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private InventoryBatchRepository batchRepository;
    @Autowired
    private InventoryHandlerFactory handlerFactory;

    @Override
    public List<InventoryBatchDto> getBatchesByProductSku(String sku) {
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new IllegalArgumentException("Product not found for sku=" + sku));
        List<InventoryBatch> batches = batchRepository.findByProductOrderByExpiryDateAsc(product);
        return batches.stream()
                .map(b -> InventoryBatchDto.builder()
                        .id(b.getId())
                        .sku(product.getSku())
                        .quantity(b.getQuantity())
                        .expiryDate(b.getExpiryDate())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void applyUpdate(InventoryUpdateRequest request) {
        // Select handler through factory — allows swapping policies without changing service layer.
        InventoryHandler handler = handlerFactory.getHandler("default");
        handler.handleUpdate(request);
    }
}
