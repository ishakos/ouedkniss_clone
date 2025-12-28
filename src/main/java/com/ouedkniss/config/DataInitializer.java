package com.ouedkniss.config;

import com.ouedkniss.product.model.Category;
import com.ouedkniss.product.repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/*
This avoids manually inserting categories in DB.
 */

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initCategories(CategoryRepository repo) {
        return args -> {
            if (repo.count() == 0) {
                repo.saveAll(List.of(
                        new Category("Cars"),
                        new Category("Motorcycles"),
                        new Category("Phones"),
                        new Category("Computers"),
                        new Category("Electronics"),
                        new Category("Real Estate"),
                        new Category("Jobs"),
                        new Category("Services"),
                        new Category("Furniture"),
                        new Category("Home Appliances"),
                        new Category("Fashion"),
                        new Category("Sports & Hobbies")
                ));

            }
        };
    }
}
