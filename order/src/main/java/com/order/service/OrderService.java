package com.order.service;


import com.order.dto.OrderRequest;

public interface OrderService {
    void placeOrder(OrderRequest request);
}

