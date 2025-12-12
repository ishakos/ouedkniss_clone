package com.ouedkniss.product.controller;

import com.ouedkniss.product.model.Wishlist;
import com.ouedkniss.product.service.WishlistService;
import com.ouedkniss.user.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/wishlist")
public class WishlistController {

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

    // ----------------------------------
    // TOGGLE WISHLIST
    // ----------------------------------
    @GetMapping("/toggle/{productId}")
    public String toggle(@PathVariable Long productId,
                         @RequestParam(required = false) String redirect,
                         HttpSession session) {

        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        Long userId = ((User) session.getAttribute("user")).getId();
        wishlistService.toggleWishlist(userId, productId);

        if ("wishlist".equals(redirect)) {
            return "redirect:/wishlist";
        }

        return "redirect:/";
    }


    // ----------------------------------
    // VIEW + SEARCH WITHIN WISHLIST
    // ----------------------------------
    @GetMapping
    public String viewWishlist(@RequestParam(required = false) String name,
                               @RequestParam(required = false) String city,
                               @RequestParam(required = false) Integer min,
                               @RequestParam(required = false) Integer max,
                               Model model,
                               HttpSession session) {

        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        Long userId = ((User) session.getAttribute("user")).getId();

        // Full wishlist
        List<Wishlist> list = wishlistService.getUserWishlist(userId);

        // Filter based on search
        List<Wishlist> filtered = list.stream()
                .filter(w -> (name == null || name.isEmpty()
                        || w.getProduct().getTitle().toLowerCase().contains(name.toLowerCase())))
                .filter(w -> (city == null || city.isEmpty()
                        || (w.getProduct().getCity() != null &&
                        w.getProduct().getCity().equalsIgnoreCase(city))))
                .filter(w -> (min == null || w.getProduct().getPrice() >= min))
                .filter(w -> (max == null || w.getProduct().getPrice() <= max))
                .collect(Collectors.toList());

        model.addAttribute("wishlist", filtered);
        model.addAttribute("cities", CITIES);

        return "wishlist";
    }
}
