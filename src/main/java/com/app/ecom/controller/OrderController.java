package com.app.ecom.controller;

import com.app.ecom.dto.ApiResponse;
import com.app.ecom.dto.OrderCreateDto;
import com.app.ecom.dto.OrderCreateResponseDto;
import com.app.ecom.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<OrderCreateResponseDto>> createOrder(@RequestBody OrderCreateDto order) {
        return orderService.createOrder(order);
    }
}
