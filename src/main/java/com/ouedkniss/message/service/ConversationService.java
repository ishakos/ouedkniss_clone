package com.ouedkniss.message.service;

import com.ouedkniss.message.model.Conversation;
import com.ouedkniss.message.model.Message;
import com.ouedkniss.message.repository.ConversationRepository;
import com.ouedkniss.message.repository.MessageRepository;
import com.ouedkniss.product.repository.ProductRepository;
import com.ouedkniss.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ConversationService {

    @Autowired
    private ConversationRepository convoRepo;

    @Autowired
    private MessageRepository messageRepo;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;



    public Conversation getOrCreateConversation(Long productId, Long buyerId, Long sellerId) {

        Conversation existing =
                convoRepo.findByProductIdAndBuyerIdAndSellerId(productId, buyerId, sellerId);

        if (existing != null) return existing;

        Conversation c = new Conversation();
        c.setProduct(productRepository.findById(productId).get());
        c.setBuyer(userRepository.findById(buyerId).get());
        c.setSeller(userRepository.findById(sellerId).get());


        return convoRepo.save(c);
    }


    public Conversation getConversation(Long id) {
        return convoRepo.findById(id).orElse(null);
    }


    public List<Message> getMessages(Long convoId) {
        return messageRepo.findByConversationIdOrderByTimestampAsc(convoId);
    }


    public void sendMessage(Long convoId, Long senderId, Long receiverId, String content) {

        Message m = new Message();
        m.setConversationId(convoId);
        m.setSenderId(senderId);
        m.setContent(content);
        m.setTimestamp(LocalDateTime.now());

        messageRepo.save(m);
    }


    public List<Conversation> getAllForUser(Long userId) {
        return convoRepo.findByBuyerIdOrSellerId(userId, userId);
    }
}
