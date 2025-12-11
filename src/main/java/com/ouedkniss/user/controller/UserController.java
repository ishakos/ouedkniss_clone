package com.ouedkniss.user.controller;

import com.ouedkniss.user.model.User;
import com.ouedkniss.user.repository.UserRepository;
import com.ouedkniss.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {
    @Autowired
    private UserRepository repo;

    @GetMapping("/register")
    public String showRegister() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user) {
        repo.save(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLogin() { return "login"; }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session) {

        User u = repo.findByUsername(username);

        if (u != null && u.getPassword().equals(password)) {
            session.setAttribute("user", u);
            return "redirect:/";
        }

        return "redirect:/login?error=true";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

}
