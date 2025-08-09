package com.app.ecom.repository;

import com.app.ecom.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByUserIdAndProductId(Long userId, Long productId);

    List<CartItem> findByUserId(Long userId);
}
