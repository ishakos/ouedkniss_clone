package com.ouedkniss.message.repository;

import com.ouedkniss.message.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    Conversation findByProductIdAndBuyerIdAndSellerId(Long productId, Long buyerId, Long sellerId);

    List<Conversation> findByBuyerIdOrSellerId(Long buyerId, Long sellerId);
}
