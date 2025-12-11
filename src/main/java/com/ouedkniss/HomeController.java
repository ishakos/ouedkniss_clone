package com.ouedkniss;

import com.ouedkniss.product.repository.ProductRepository;
import com.ouedkniss.product.service.ProductService;
import com.ouedkniss.user.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private ProductService productService;

    @Autowired
    private HttpSession session;

    @GetMapping("/")
    public String home(Model model) {

        User loggedUser = (User) session.getAttribute("user");

        if (loggedUser != null) {
            model.addAttribute("products",
                    productService.getProductsNotOwnedBy(loggedUser.getId()));
        } else {
            model.addAttribute("products", productService.getAllProducts());
        }

        return "index";
    }


}
