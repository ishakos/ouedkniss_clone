package com.ouedkniss.product.service;

import com.ouedkniss.product.model.Category;
import com.ouedkniss.product.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository repo;

    public CategoryService(CategoryRepository repo) {
        this.repo = repo;
    }

    public List<Category> findAll() {
        return repo.findAll();
    }

    public Category findById(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Category save(Category category) {
        return repo.save(category);
    }
}
