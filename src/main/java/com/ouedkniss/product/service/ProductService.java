package com.ouedkniss.product.service;

import com.ouedkniss.product.model.Category;
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

    public List<Product> search(String name, String city, Integer min, Integer max, Category categoryId) {

        List<Product> all = repo.findAll();

        return all.stream()
                .filter(p -> name == null || name.isEmpty() ||
                        p.getTitle().toLowerCase().contains(name.toLowerCase()))
                .filter(p -> city == null || city.isEmpty() ||
                        p.getCity().equals(city))
                .filter(p -> min == null || p.getPrice() >= min)
                .filter(p -> max == null || p.getPrice() <= max)
                .filter(p -> categoryId == null ||
                        p.getCategory().equals(categoryId))
                .toList();
    }

}


