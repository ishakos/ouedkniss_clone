package com.ouedkniss.message.repository;

import com.ouedkniss.message.model.Message;
import com.ouedkniss.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByProduct(Product product);
}
