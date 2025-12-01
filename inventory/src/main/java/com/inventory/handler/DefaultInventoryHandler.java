package com.inventory.handler;

import com.inventory.dto.InventoryUpdateRequest;
import com.inventory.model.InventoryBatch;
import com.inventory.model.Product;
import com.inventory.repository.InventoryBatchRepository;
import com.inventory.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DefaultInventoryHandler implements InventoryHandler
{
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private InventoryBatchRepository batchRepository;


    @Override
    @Transactional
    public void handleUpdate(InventoryUpdateRequest request)
    {
// For this default handler we will pick FEFO (first-expiring-first-out) semantics
// when batchId not provided, otherwise adjust the specified batch.

        Optional<Product> prodOpt = productRepository.findBySku(request.getSku());
        if (prodOpt.isEmpty()) {
            throw new IllegalArgumentException("Unknown SKU: " + request.getSku());
        }
        Product product = prodOpt.get();


// Simple approach: iterate provided batch updates and apply changes.
// If batchId is null and quantityChange < 0, try to deduct from earliest-expiring batches.
        for (InventoryUpdateRequest.BatchUpdate bu : request.getBatches())
        {
            if (bu.getBatchId() != null)
            {
                try
                {
                    InventoryBatch batch = batchRepository.findById(bu.getBatchId()).get();
                    batch.setQuantity(Math.max(0, batch.getQuantity() + bu.getQuantityChange()));
                    batchRepository.save(batch);
                }
                catch (Exception e)
                {
                    throw new IllegalArgumentException("Unknown batchId: " + e.getMessage() + bu.getBatchId());
                }
            }
            else
            {
                int remaining = Math.abs(bu.getQuantityChange());
                if (bu.getQuantityChange() < 0)
                {
                    List<InventoryBatch> batches = batchRepository.findByProductOrderByExpiryDateAsc(product);
                    for (InventoryBatch batch : batches)
                    {
                        if (remaining <= 0) break;
                        if (batch.isExpired()) continue; // skip expired
                        int take = Math.min(batch.getQuantity(), remaining);
                        batch.setQuantity(batch.getQuantity() - take);
                        remaining -= take;
                        batchRepository.save(batch);
                    }
                    if (remaining > 0)
                    {
                        throw new IllegalStateException("Insufficient inventory for SKU=" + request.getSku());
                    }
                }
            }
        }
    }
}