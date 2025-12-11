package com.ouedkniss.user.controller;

import com.ouedkniss.user.model.User;
import com.ouedkniss.user.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    private final UserRepository repo;

    public UserController(UserRepository repo) {
        this.repo = repo;
    }

    // ==========================
    //       REGISTER
    // ==========================
    @GetMapping("/register")
    public String showRegister(HttpSession session) {
        if (session.getAttribute("user") != null) {
            return "redirect:/"; // already logged in → go home
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String name,
                           @RequestParam String password,
                           @RequestParam String email,
                           @RequestParam String phone,
                           HttpSession session) {

        // Length check
        if (username.length() < 3 || name.length() < 3 || password.length() < 3)
            return "redirect:/register?error=short";

        // Email check
        if (!email.contains("@"))
            return "redirect:/register?error=email";

        // Phone check: 05 / 06 / 07 — total 10 digits
        if (!phone.matches("0[567][0-9]{8}"))
            return "redirect:/register?error=phone";

        // Username must be unique
        if (repo.findByUsername(username) != null)
            return "redirect:/register?error=exists";

        // Create user
        User u = new User();
        u.setUsername(username);
        u.setName(name);
        u.setPassword(password);
        u.setEmail(email);
        u.setPhone(phone);

        repo.save(u);

        // Auto login
        session.setAttribute("user", u);

        return "redirect:/";
    }

    // ==========================
    //         LOGIN
    // ==========================
    @GetMapping("/login")
    public String showLogin(HttpSession session) {
        if (session.getAttribute("user") != null) {
            return "redirect:/"; // already logged in → go home
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session) {

        User u = repo.findByUsername(username);

        if (u == null)
            return "redirect:/login?error=username";

        if (!u.getPassword().equals(password))
            return "redirect:/login?error=password";

        session.setAttribute("user", u);
        return "redirect:/";
    }

    // ==========================
    //       PROFILE PAGE
    // ==========================
    @GetMapping("/profile")
    public String profilePage(HttpSession session, Model model) {
        User u = (User) session.getAttribute("user");

        if (u == null)
            return "redirect:/login";

        model.addAttribute("user", u);
        return "edit-profile"; // Only ONE profile page
    }

    // ==========================
    //     UPDATE PROFILE
    // ==========================
    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam String name,
                                @RequestParam String email,
                                @RequestParam String phone,
                                @RequestParam(required = false) String password,
                                HttpSession session) {

        User u = (User) session.getAttribute("user");
        if (u == null) return "redirect:/login";

        // Phone validation again
        if (!phone.matches("0[567][0-9]{8}"))
            return "redirect:/profile?error=phone";

        u.setName(name);
        u.setEmail(email);
        u.setPhone(phone);

        if (password != null && !password.isEmpty())
            u.setPassword(password);

        repo.save(u);

        session.setAttribute("user", u);

        return "redirect:/profile?success=true";
    }

    // ==========================
    //         LOGOUT
    // ==========================
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
