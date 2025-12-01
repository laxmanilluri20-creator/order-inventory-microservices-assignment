package com.inventory.service;

import com.inventory.dto.InventoryBatchDto;
import com.inventory.dto.InventoryUpdateRequest;
import com.inventory.handler.InventoryHandler;
import com.inventory.handler.InventoryHandlerFactory;
import com.inventory.model.InventoryBatch;
import com.inventory.model.Product;
import com.inventory.repository.InventoryBatchRepository;
import com.inventory.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class InventoryServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InventoryBatchRepository batchRepository;

    @Mock
    private InventoryHandlerFactory handlerFactory;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getBatchesByProductSku_success() {
        Product product = Product.builder().id(1L).sku("SKU-1").name("P1").build();
        InventoryBatch b1 = InventoryBatch.builder().id(10L).product(product).quantity(5).expiryDate(LocalDate.now().plusDays(5)).build();
        InventoryBatch b2 = InventoryBatch.builder().id(11L).product(product).quantity(3).expiryDate(LocalDate.now().plusDays(10)).build();

        when(productRepository.findBySku("SKU-1")).thenReturn(Optional.of(product));
        when(batchRepository.findByProductOrderByExpiryDateAsc(product)).thenReturn(List.of(b1, b2));

        List<InventoryBatchDto> dtos = inventoryService.getBatchesByProductSku("SKU-1");

        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0).getId()).isEqualTo(10L);
        verify(productRepository).findBySku("SKU-1");
        verify(batchRepository).findByProductOrderByExpiryDateAsc(product);
    }

    @Test
    void applyUpdate_usesFactoryHandler() {
        // When applyUpdate is called, the service should get handler from factory and call it.
        InventoryUpdateRequest request = new InventoryUpdateRequest();
        request.setSku("SKU-1");
        request.setBatches(List.of(new InventoryUpdateRequest.BatchUpdate(null, -5)));

        InventoryHandler handler = mock(InventoryHandler.class);
        when(handlerFactory.getHandler(anyString())).thenReturn(handler);

        inventoryService.applyUpdate(request);

        verify(handlerFactory).getHandler("default");
        verify(handler).handleUpdate(request);
    }
}
