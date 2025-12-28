package com.ouedkniss.product.repository;

import com.ouedkniss.product.model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    Wishlist findByUserIdAndProductId(Long userId, Long productId);

    List<Wishlist> findByUserId(Long userId);

    void deleteByUserIdAndProductId(Long userId, Long productId);

    boolean existsByUserIdAndProductId(Long userId, Long productId);
}
