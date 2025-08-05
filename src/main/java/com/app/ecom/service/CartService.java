package com.app.ecom.service;

import com.app.ecom.dto.AddToCartRequestDto;
import com.app.ecom.dto.ApiResponse;
import com.app.ecom.dto.CartItemResponseDto;
import com.app.ecom.model.CartItem;
import com.app.ecom.model.Product;
import com.app.ecom.model.User;
import com.app.ecom.repository.CartItemRepository;
import com.app.ecom.repository.ProductRepository;
import com.app.ecom.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public ResponseEntity<ApiResponse<Map<String, Object>>> addToCart(AddToCartRequestDto requestDto) {
        try {
            User user = userRepository.findById(requestDto.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Product product = productRepository.findById(requestDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            Optional<CartItem> existingCartItemOpt = cartItemRepository
                    .findByUserIdAndProductId(user.getId(), product.getId());

            CartItem cartItem;
            if (existingCartItemOpt.isPresent()) {
                cartItem = existingCartItemOpt.get();
                int newQuantity = cartItem.getQuantity() + requestDto.getQuantity();
                cartItem.setQuantity(newQuantity);
                cartItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(newQuantity)));
            } else {
                cartItem = new CartItem();
                cartItem.setUser(user);
                cartItem.setProduct(product);
                cartItem.setQuantity(requestDto.getQuantity());
                cartItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(requestDto.getQuantity())));
            }

            CartItem savedItem = cartItemRepository.save(cartItem);

            CartItemResponseDto responseDto = CartItemResponseDto.builder()
                    .id(savedItem.getId())
                    .userId(user.getId())
                    .productId(product.getId())
                    .productName(product.getName())
                    .quantity(savedItem.getQuantity())
                    .price(savedItem.getPrice())
                    .build();

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("cartItemDetails", responseDto);

            ApiResponse<Map<String, Object>> response = new ApiResponse<>(true, "Cart item added successfully", responseMap);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            ApiResponse<Map<String, Object>> errorResponse = new ApiResponse<>(false, e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            ApiResponse<Map<String, Object>> errorResponse = new ApiResponse<>(false, "Internal server error", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    public ResponseEntity<ApiResponse<Map<String, Object>>> removeFromCart(Long userId, Long productId) {
        try {
            Optional<CartItem> existingCartItemOpt = cartItemRepository.findByUserIdAndProductId(userId, productId);

            if (existingCartItemOpt.isEmpty()) {
                ApiResponse<Map<String, Object>> response = new ApiResponse<>(
                        false, "Cart item not found", null
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            CartItem cartItem = existingCartItemOpt.get();
            cartItemRepository.delete(cartItem);

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("removedCartItem", CartItemResponseDto.builder()
                    .id(cartItem.getId())
                    .userId(cartItem.getUser().getId())
                    .productId(cartItem.getProduct().getId())
                    .productName(cartItem.getProduct().getName())
                    .quantity(cartItem.getQuantity())
                    .price(cartItem.getPrice())
                    .build());

            ApiResponse<Map<String, Object>> response = new ApiResponse<>(
                    true, "Cart item removed successfully", responseMap
            );
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ApiResponse<Map<String, Object>> errorResponse = new ApiResponse<>(
                    false, "Internal server error", null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCartByUserId(Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<CartItem> cartItems = cartItemRepository.findByUserId(user.getId());

            List<CartItemResponseDto> cartItemDtos = cartItems.stream()
                    .map(item -> CartItemResponseDto.builder()
                            .id(item.getId())
                            .userId(user.getId())
                            .productId(item.getProduct().getId())
                            .productName(item.getProduct().getName())
                            .quantity(item.getQuantity())
                            .price(item.getPrice())
                            .build())
                    .toList();

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("cartItems", cartItemDtos);

            ApiResponse<Map<String, Object>> response = new ApiResponse<>(true, "Cart items fetched successfully", responseMap);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            ApiResponse<Map<String, Object>> errorResponse = new ApiResponse<>(false, e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            ApiResponse<Map<String, Object>> errorResponse = new ApiResponse<>(false, "Internal server error", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

}
