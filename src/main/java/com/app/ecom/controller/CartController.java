package com.app.ecom.controller;

import com.app.ecom.dto.*;
import com.app.ecom.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Map<String, Object>>> addToCart(@RequestBody AddToCartRequestDto requestDto) {
        return cartService.addToCart(requestDto);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<ApiResponse<Map<String, Object>>> removeFromCart(
            @RequestParam Long userId,
            @RequestParam Long productId) {
        return cartService.removeFromCart(userId, productId);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCartByUserId(@PathVariable Long userId) {
        return cartService.getCartByUserId(userId);
    }
}
