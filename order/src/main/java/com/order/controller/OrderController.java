package com.order.controller;

import com.order.dto.OrderRequest;
import com.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<String> placeOrder(@Valid @RequestBody OrderRequest request) {
        orderService.placeOrder(request);
        return ResponseEntity.ok("Order placed: " + request.getOrderId());
    }
}
