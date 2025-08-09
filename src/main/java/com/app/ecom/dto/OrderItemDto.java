package com.app.ecom.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderItemDto {
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
}
