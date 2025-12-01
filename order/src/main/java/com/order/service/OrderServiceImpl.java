package com.order.service;

import com.order.dto.InventoryBatchDto;
import com.order.dto.InventoryUpdateRequest;
import com.order.dto.OrderItem;
import com.order.dto.OrderRequest;
import com.order.model.OrderEntity;
import com.order.model.OrderItemEntity;
import com.order.repository.OrderRepository;
import com.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    @Autowired
    private  RestTemplate restTemplate;
    @Autowired
    private final OrderRepository orderRepository;

    @Value("${inventory.service.base-url}")
    private String inventoryBaseUrl;

    private static final String INVENTORY_GET_TEMPLATE = "%s/inventory/%s";
    private static final String INVENTORY_UPDATE_URL = "%s/inventory/update";

    /**
     * Places an order:
     * 1. Checks availability by calling Inventory Service GET /inventory/{sku}
     * 2. If enough across batches, posts an inventory update to deduct quantities
     * 3. Persists order as COMPLETED. On failure, throws and transaction rolls back.
     */
    @Override
    @Transactional
    public void placeOrder(OrderRequest request)
    {
        // Idempotency: if orderId already exists, ignore or return
        Optional<OrderEntity> existing = orderRepository.findByOrderId(request.getOrderId());
        if (existing.isPresent())
        {
            //do nothing (could also return a status)
            return;
        }

        Map<String, Integer> total_required_quantity = new HashMap<>();
        for (OrderItem item : request.getItems())
        {
            total_required_quantity.merge(item.getSku(), item.getQuantity(), Integer::sum);
        }

        // For each SKU, query inventory and ensure sum(quantity) >= needed
        for (Map.Entry<String, Integer> e : total_required_quantity.entrySet()) {
            String sku = e.getKey();
            int required = e.getValue();
            String getUrl = String.format(INVENTORY_GET_TEMPLATE, inventoryBaseUrl, sku);
            ResponseEntity<InventoryBatchDto[]> resp;
            try
            {
                resp = restTemplate.getForEntity(getUrl, InventoryBatchDto[].class);
            }
            catch (RestClientException ex)
            {
                throw new IllegalStateException("Failed to contact inventory service for sku=" + sku, ex);
            }

            if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null)
            {
                throw new IllegalStateException("Inventory service returned error for sku=" + sku);
            }
            int available = Arrays.stream(resp.getBody())
                    .filter(b -> b.getQuantity() != null)
                    .mapToInt(InventoryBatchDto::getQuantity)
                    .sum();

            if (available < required)
            {
                throw new IllegalStateException("Insufficient stock for sku=" + sku + " required=" + required + " available=" + available);
            }
        }

        // Build and send deduction requests to Inventory Service
        // For each order item we will send
        // InventoryUpdateRequest with single BatchUpdate { batchId:null, quantityChange:-qty }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        for (OrderItem item : request.getItems())
        {
            InventoryUpdateRequest updateReq = new InventoryUpdateRequest();
            updateReq.setSku(item.getSku());
            InventoryUpdateRequest.BatchUpdate bu = new InventoryUpdateRequest.BatchUpdate(null, -item.getQuantity());
            updateReq.setBatches(Collections.singletonList(bu));

            String updateUrl = String.format(INVENTORY_UPDATE_URL, inventoryBaseUrl);
            HttpEntity<InventoryUpdateRequest> entity = new HttpEntity<>(updateReq, headers);

            ResponseEntity<Void> updateResp;
            try {
                updateResp = restTemplate.postForEntity(updateUrl, entity, Void.class);
            } catch (RestClientException ex) {
                throw new IllegalStateException("Failed to update inventory for sku=" + item.getSku(), ex);
            }
            if (!updateResp.getStatusCode().is2xxSuccessful())
            {
                throw new IllegalStateException("Inventory update failed for sku=" + item.getSku());
            }
        }

        //Persist order and set status as COMPLETED
        OrderEntity order = OrderEntity.builder()
                .orderId(request.getOrderId())
                .createdAt(OffsetDateTime.now())
                .status("COMPLETED")
                .build();

        for ( OrderItem it : request.getItems() ) {
            OrderItemEntity entity = OrderItemEntity.builder()
                    .sku(it.getSku())
                    .quantity(it.getQuantity())
                    .build();
            order.addItem(entity);
        }

        orderRepository.save(order);
    }
}
