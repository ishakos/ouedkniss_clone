package com.ouedkniss;

import com.ouedkniss.product.model.Product;
import com.ouedkniss.product.service.ProductService;
import com.ouedkniss.product.service.WishlistService;
import com.ouedkniss.user.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ProductService productService;

    @Autowired
    private WishlistService wishlistService;

    private static final List<String> CITIES = List.of(
            "Adrar", "Chlef", "Laghouat", "Oum El Bouaghi", "Batna", "Bejaia",
            "Biskra", "Bechar", "Blida", "Bouira", "Tamanrasset", "Tebessa",
            "Tlemcen", "Tiaret", "Tizi Ouzou", "Alger", "Djelfa", "Jijel",
            "Setif", "Saida", "Skikda", "Sidi Bel Abbes", "Annaba", "Guelma",
            "Constantine", "Medea", "Mostaganem", "MSila", "Mascara", "Ouargla",
            "Oran", "El Bayadh", "Illizi", "Bordj Bou Arreridj", "Boumerdes",
            "El Tarf", "Tindouf", "Tissemsilt", "El Oued", "Khenchela",
            "Souk Ahras", "Tipaza", "Mila", "Ain Defla", "Naama", "Ain Temouchent",
            "Ghardaia", "Relizane", "Timimoun", "Bordj Badji Mokhtar",
            "Beni Abbes", "In Salah", "In Guezzam", "Touggourt",
            "Djanet", "El Mghair", "El Meniaa"
    );

    @GetMapping("/")
    public String home(@RequestParam(required = false) String name,
                       @RequestParam(required = false) String city,
                       @RequestParam(required = false) Integer min,
                       @RequestParam(required = false) Integer max,
                       Model model,
                       HttpSession session) {

        User user = (User) session.getAttribute("user");
        List<Product> products;

        boolean searching =
                (name != null && !name.isEmpty()) ||
                        (city != null && !city.isEmpty()) ||
                        min != null ||
                        max != null;

        if (searching) {
            // Search normally
            products = productService.search(name, city, min, max);

            // If logged in → exclude user's own products
            if (user != null) {
                products.removeIf(p -> p.getUser().getId().equals(user.getId()));
            }

        } else {
            // Default behavior
            if (user != null) {
                products = productService.getProductsNotOwnedBy(user.getId());
            } else {
                products = productService.getAllProducts();
            }
        }

        // Mark wishlist entries
        if (user != null) {
            for (Product p : products) {
                boolean saved = wishlistService.isInWishlist(user.getId(), p.getId());
                p.setSaved(saved);
            }
        }

        model.addAttribute("products", products);
        model.addAttribute("cities", CITIES);

        return "index";
    }
}
