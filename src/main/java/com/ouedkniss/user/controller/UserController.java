package com.ouedkniss.user.controller;

import com.ouedkniss.user.model.User;
import com.ouedkniss.user.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    @Autowired
    private UserRepository repo;


    // =============================
    // REGISTER PAGE (GET)
    // =============================
    @GetMapping("/register")
    public String showRegister(HttpSession session) {

        // If already logged in → redirect home
        if (session.getAttribute("user") != null) {
            return "redirect:/";
        }

        return "register";
    }

    // =============================
    // REGISTER SUBMIT (POST)
    // =============================
    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String name,
                           @RequestParam String password,
                           HttpSession session) {

        // Validate lengths
        if (username.length() < 3 || name.length() < 3 || password.length() < 3) {
            return "redirect:/register?error=short";
        }

        // Unique username
        if (repo.findByUsername(username) != null) {
            return "redirect:/register?error=exists";
        }

        // Create user
        User u = new User();
        u.setUsername(username);
        u.setName(name);
        u.setPassword(password);
        repo.save(u);

        // Auto login
        session.setAttribute("user", u);

        return "redirect:/login";
    }


    // =============================
    // LOGIN PAGE (GET)
    // =============================
    @GetMapping("/login")
    public String showLogin(HttpSession session) {

        // If already logged in → redirect home
        if (session.getAttribute("user") != null) {
            return "redirect:/";
        }

        return "login";
    }

    // =============================
    // LOGIN SUBMIT (POST)
    // =============================
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session) {

        User u = repo.findByUsername(username);

        // Username not found
        if (u == null) {
            return "redirect:/login?error=username";
        }

        // Wrong password
        if (!u.getPassword().equals(password)) {
            return "redirect:/login?error=password";
        }

        // Login OK
        session.setAttribute("user", u);
        return "redirect:/";
    }


    // =============================
    // LOGOUT
    // =============================
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

}
