package com.ouedkniss.product.controller;

import com.ouedkniss.product.model.Product;
import com.ouedkniss.product.service.ProductService;
import com.ouedkniss.user.model.User;
import com.ouedkniss.user.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final UserRepository userRepo;

    public ProductController(ProductService productService, UserRepository userRepo) {
        this.productService = productService;
        this.userRepo = userRepo;
    }

    // ✅ THIS PAGE IS NOW AT:  /products
    @GetMapping
    public String listProducts(Model model, HttpSession session) {

        User loggedUser = (User) session.getAttribute("user");

        // DEBUG LOGS
        if (loggedUser != null) {
            System.out.println("Logged user ID = " + loggedUser.getId());

            for (Product p : productService.getAllProducts()) {
                System.out.println(
                        "Product " + p.getId() + " owner: " +
                                (p.getUser() == null ? "NULL" : p.getUser().getId())
                );
            }

            model.addAttribute("products",
                    productService.getProductsNotOwnedBy(loggedUser.getId()));
        } else {
            model.addAttribute("products", productService.getAllProducts());
        }

        return "products"; // <-- make sure you have products.html
    }

    @PostMapping
    public String createProduct(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam Double price,
            @RequestParam Long userId
    ) {
        var user = userRepo.findById(userId).orElse(null);
        var product = new Product(title, description, price, user);
        productService.saveProduct(product);
        return "redirect:/products";
    }

    @PostMapping("/add")
    public String addProduct(@ModelAttribute Product p,
                             @RequestParam("imageFile") MultipartFile imageFile,
                             HttpSession session) throws IOException {

        User user = (User) session.getAttribute("user");

        if (user == null)
            return "redirect:/login";

        // CREATE UPLOAD FOLDER
        String uploadDir = "uploads/images/";
        Files.createDirectories(Paths.get(uploadDir));

        // SAVE FILE
        String fileName = imageFile.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + fileName);
        Files.write(filePath, imageFile.getBytes());

        // SAVE PRODUCT
        p.setImage(fileName);
        p.setOwner(user.getUsername());
        p.setUser(user);

        productService.saveProduct(p);

        return "redirect:/products";
    }

    @GetMapping("/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("users", userRepo.findAll());
        return "addProduct";
    }
}
