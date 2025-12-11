package com.ouedkniss.message.controller;

import com.ouedkniss.message.model.Message;
import com.ouedkniss.message.service.MessageService;
import com.ouedkniss.product.service.ProductService;
import com.ouedkniss.user.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;
    private final UserRepository userRepo;
    private final ProductService productService;

    public MessageController(MessageService messageService, UserRepository userRepo, ProductService productService) {
        this.messageService = messageService;
        this.userRepo = userRepo;
        this.productService = productService;
    }

    @GetMapping("/{productId}")
    public String viewMessages(@PathVariable Long productId, Model model) {
        var product = productService.getById(productId);
        if (product == null) {
            model.addAttribute("error", "Product does not exist.");
            return "messages"; // or redirect to /products
        }
        model.addAttribute("product", product);
        model.addAttribute("messages", messageService.getByProduct(product));
        model.addAttribute("users", userRepo.findAll());
        return "messages";
    }

    @PostMapping
    public String sendMessage(
            @RequestParam Long productId,
            @RequestParam Long senderId,
            @RequestParam Long receiverId,
            @RequestParam String content
    ) {
        var sender = userRepo.findById(senderId).orElse(null);
        var receiver = userRepo.findById(receiverId).orElse(null);
        var product = productService.getById(productId);

        messageService.save(new Message(content, sender, receiver, product));
        return "redirect:/messages/" + productId;
    }

    @GetMapping("/messages/send")
    public String sendMessagePage(@RequestParam String receiver,
                                  HttpSession session,
                                  Model model) {

        if (session.getAttribute("user") == null)
            return "redirect:/login";

        model.addAttribute("receiver", receiver);
        return "sendMessage";
    }

}

