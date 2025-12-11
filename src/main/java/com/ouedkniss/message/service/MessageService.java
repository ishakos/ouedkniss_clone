package com.ouedkniss.message.service;

import com.ouedkniss.message.model.Message;
import com.ouedkniss.message.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    private final MessageRepository repo;

    public MessageService(MessageRepository repo) {
        this.repo = repo;
    }

    public List<Message> getMessages(Long conversationId) {
        return repo.findByConversationIdOrderByTimestampAsc(conversationId);
    }

    public void saveMessage(Message message) {
        repo.save(message);
    }
}
