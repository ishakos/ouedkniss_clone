package com.ouedkniss.product.service;

import com.ouedkniss.product.model.Wishlist;
import com.ouedkniss.product.repository.ProductRepository;
import com.ouedkniss.product.repository.WishlistRepository;
import com.ouedkniss.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WishlistService {

    @Autowired
    WishlistRepository wishlistRepo;

    @Autowired
    UserRepository userRepo;

    @Autowired
    ProductRepository productRepo;

    public boolean toggleWishlist(Long userId, Long productId) {
        Wishlist w = wishlistRepo.findByUserIdAndProductId(userId, productId);

        if (w != null) {
            wishlistRepo.delete(w);
            return false; // removed
        }

        Wishlist newWish = new Wishlist();
        newWish.setUser(userRepo.findById(userId).get());
        newWish.setProduct(productRepo.findById(productId).get());

        wishlistRepo.save(newWish);
        return true; // added
    }

    public List<Wishlist> getUserWishlist(Long userId) {
        return wishlistRepo.findByUserId(userId);
    }

    public boolean isInWishlist(Long userId, Long productId) {
        return wishlistRepo.existsByUserIdAndProductId(userId, productId);
    }

}

