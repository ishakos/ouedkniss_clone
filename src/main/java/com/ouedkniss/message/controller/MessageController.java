package com.ouedkniss.message.controller;

import com.ouedkniss.message.model.Conversation;
import com.ouedkniss.message.service.ConversationService;
import com.ouedkniss.product.model.Product;
import com.ouedkniss.product.repository.ProductRepository;
import com.ouedkniss.user.model.User;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private ConversationService convoService;

    @Autowired
    private ProductRepository productRepo;


    @GetMapping("/start/{productId}")
    public String startConversation(@PathVariable Long productId, HttpSession session) {

        User current = (User) session.getAttribute("user");
        if (current == null) return "redirect:/login";

        Product p = productRepo.findById(productId).orElse(null);
        if (p == null) return "redirect:/";

        Long buyerId = current.getId();
        Long sellerId = p.getUser().getId();

        if (buyerId.equals(sellerId))
            return "redirect:/products/" + productId;

        Conversation c = convoService.getOrCreateConversation(productId, buyerId, sellerId);

        return "redirect:/messages/chat/" + c.getId();
    }


    @GetMapping("/chat/{id}")
    public String chat(@PathVariable Long id, HttpSession session, Model model) {

        User current = (User) session.getAttribute("user");
        if (current == null) return "redirect:/login";

        Conversation c = convoService.getConversation(id);
        if (c == null) return "redirect:/messages";

        model.addAttribute("conversationId", id);
        model.addAttribute("messages", convoService.getMessages(id));
        model.addAttribute("currentUserId", current.getId());

        return "chat";
    }


    @PostMapping("/send")
    public String sendMessage(@RequestParam Long conversationId,
                              @RequestParam String content,
                              HttpSession session) {

        User sender = (User) session.getAttribute("user");
        if (sender == null) return "redirect:/login";

        Conversation c = convoService.getConversation(conversationId);
        if (c == null) return "redirect:/messages";

        Long receiverId =
                sender.getId().equals(c.getBuyer().getId()) ? c.getSeller().getId() : c.getBuyer().getId();

        convoService.sendMessage(conversationId, sender.getId(), receiverId, content);

        return "redirect:/messages/chat/" + conversationId;
    }


    @GetMapping
    public String list(Model model, HttpSession session) {

        User u = (User) session.getAttribute("user");
        if (u == null) return "redirect:/login";

        model.addAttribute("conversations", convoService.getAllForUser(u.getId()));

        return "conversations";
    }
}
