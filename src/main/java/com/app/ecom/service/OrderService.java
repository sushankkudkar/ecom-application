package com.app.ecom.service;

import com.app.ecom.dto.*;
import com.app.ecom.model.*;
import com.app.ecom.repository.CartItemRepository;
import com.app.ecom.repository.OrderRepository;
import com.app.ecom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;

    public ResponseEntity<ApiResponse<OrderCreateResponseDto>> createOrder(OrderCreateDto request) {
        try {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<CartItem> cartItems = cartItemRepository.findByUserId(user.getId());
            if (cartItems.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(false, "Cart is empty", null));
            }

            Order order = new Order();
            order.setUser(user);
            order.setCreatedAt(LocalDateTime.now());
            order.setUpdatedAt(LocalDateTime.now());
            order.setStatus(OrderStatus.CONFIRMED);

            List<OrderItem> orderItems = new ArrayList<>();
            List<OrderItemDto> orderItemDtos = new ArrayList<>();
            BigDecimal totalAmount = BigDecimal.ZERO;

            for (CartItem cartItem : cartItems) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setProduct(cartItem.getProduct());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setPrice(cartItem.getPrice());

                orderItems.add(orderItem);
                totalAmount = totalAmount.add(cartItem.getPrice());

                orderItemDtos.add(OrderItemDto.builder()
                        .productId(cartItem.getProduct().getId())
                        .productName(cartItem.getProduct().getName())
                        .quantity(cartItem.getQuantity())
                        .price(cartItem.getPrice())
                        .build());
            }

            order.setItems(orderItems);
            order.setTotalAmount(totalAmount);

            Order savedOrder = orderRepository.save(order);

            // Clear user's cart after order
            cartItemRepository.deleteAll(cartItems);

            OrderCreateResponseDto responseDto = OrderCreateResponseDto.builder()
                    .orderId(savedOrder.getId())
                    .totalAmount(savedOrder.getTotalAmount())
                    .status(savedOrder.getStatus().name())
                    .createdAt(savedOrder.getCreatedAt())
                    .items(orderItemDtos)
                    .build();

            ApiResponse<OrderCreateResponseDto> response = new ApiResponse<>(true, "Order placed successfully", responseDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            ApiResponse<OrderCreateResponseDto> error = new ApiResponse<>(false, e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            ApiResponse<OrderCreateResponseDto> error = new ApiResponse<>(false, "Internal server error", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
