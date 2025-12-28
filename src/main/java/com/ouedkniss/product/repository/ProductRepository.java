package com.ouedkniss.product.repository;

import com.ouedkniss.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByUser_IdNot(Long userId);
    List<Product> findByUserId(Long userId);

    @Query("SELECT p FROM Product p WHERE "
            + "(:name IS NULL OR p.title LIKE %:name%) AND "
            + "(:city IS NULL OR p.city LIKE %:city%) AND "
            + "(:min IS NULL OR p.price >= :min) AND "
            + "(:max IS NULL OR p.price <= :max) AND "
            + "(:categoryId IS NULL OR p.category.id = :categoryId)"
    )
    List<Product> advancedSearch(
            @Param("name") String name,
            @Param("city") String city,
            @Param("min") Double min,
            @Param("max") Double max,
            @Param("categoryId") Long categoryId
    );


}



