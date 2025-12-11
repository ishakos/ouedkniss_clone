package com.ouedkniss.product.service;

import com.ouedkniss.product.model.Product;
import com.ouedkniss.product.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    public Product saveProduct(Product product) {
        return repo.save(product);
    }

    public List<Product> getAllProducts() {
        return repo.findAll();
    }

    public Product getById(Long id) {
        return repo.findById(id).orElse(null);
    }

    public List<Product> getProductsNotOwnedBy(Long userId) {
        return repo.findByUser_IdNot(userId);
    }
}


