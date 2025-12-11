package com.ouedkniss.message.service;

import com.ouedkniss.message.model.Message;
import com.ouedkniss.message.repository.MessageRepository;
import com.ouedkniss.product.model.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    private final MessageRepository repo;

    public MessageService(MessageRepository repo) {
        this.repo = repo;
    }

    public Message save(Message message) {
        return repo.save(message);
    }

    public List<Message> getByProduct(Product product) {
        return repo.findByProduct(product);
    }
}
