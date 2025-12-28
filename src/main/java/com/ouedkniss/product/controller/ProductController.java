package com.ouedkniss.product.controller;

import com.ouedkniss.product.model.Category;
import com.ouedkniss.product.model.Product;
import com.ouedkniss.product.repository.ProductRepository;
import com.ouedkniss.product.service.CategoryService;
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
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final ProductRepository productRepo;
    private final UserRepository userRepo;
    private final CategoryService categoryService;



    private static final List<String> CITIES = List.of("Adrar", "Chlef", "Laghouat", "Oum El Bouaghi", "Batna", "Bejaia",
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


    public ProductController(ProductService productService,
                             ProductRepository productRepo,
                             UserRepository userRepo,
                             CategoryService categoryService) {
        this.productService = productService;
        this.productRepo = productRepo;
        this.userRepo = userRepo;
        this.categoryService = categoryService;
    }


    /* ===========================================
       MY PRODUCTS PAGE  --->   /products
    ============================================ */
    @GetMapping
    public String myProducts(Model model, HttpSession session) {

        User loggedUser = (User) session.getAttribute("user");

        if (loggedUser == null)
            return "redirect:/login";

        List<Product> myProducts = productRepo.findByUserId(loggedUser.getId());
        model.addAttribute("products", myProducts);

        return "products";   // your products.html file
    }



    /* ===========================================
       ADD PRODUCT  ---> /products/add
    ============================================ */
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("cities", CITIES);
        model.addAttribute("categories", categoryService.findAll());
        return "add-product";
    }


    @PostMapping("/add")
    public String addProduct(@ModelAttribute Product p,
                             @RequestParam("categoryId") Long categoryId,
                             @RequestParam("imageFile") MultipartFile imageFile,
                             HttpSession session) throws IOException {

        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        Category category = categoryService.findById(categoryId);
        p.setCategory(category);

        String dir = "uploads/images/";
        Files.createDirectories(Paths.get(dir));

        String fileName = imageFile.getOriginalFilename();
        Files.write(Paths.get(dir + fileName), imageFile.getBytes());

        p.setUser(user);
        p.setOwner(user.getUsername());
        p.setImage(fileName);

        productService.saveProduct(p);

        return "redirect:/products";
    }




    /* ===========================================
       EDIT PRODUCT ---> /products/edit/{id}
    ============================================ */
    @GetMapping("/edit/{id}")
    public String editProduct(@PathVariable Long id, Model model, HttpSession session) {

        User loggedUser = (User) session.getAttribute("user");
        if (loggedUser == null) return "redirect:/login";

        Product p = productRepo.findById(id).orElse(null);

        if (p == null || !p.getUser().getId().equals(loggedUser.getId()))
            return "redirect:/products"; // forbidden

        model.addAttribute("product", p);
        model.addAttribute("cities", CITIES);
        model.addAttribute("categories", categoryService.findAll());
        return "edit-product";
    }

    @PostMapping("/update")
    public String updateProduct(@ModelAttribute Product product,
                                @RequestParam("categoryId") Long categoryId,
                                @RequestParam("imageFile") MultipartFile imageFile,
                                HttpSession session) throws IOException {

        User user = (User) session.getAttribute("user");

        if (user == null)
            return "redirect:/login";

        // Load existing product
        Product existing = productRepo.findById(product.getId()).orElse(null);

        if (existing == null || !existing.getUser().getId().equals(user.getId()))
            return "redirect:/products";

        existing.setTitle(product.getTitle());
        existing.setDescription(product.getDescription());
        existing.setPrice(product.getPrice());

        // ✅ Update category
        Category category = categoryService.findById(categoryId); // fetch managed Category from DB
        existing.setCategory(category);

        // if a new file was uploaded
        if (!imageFile.isEmpty()) {
            String uploadDir = "uploads/images/";
            Files.createDirectories(Paths.get(uploadDir));

            String filename = imageFile.getOriginalFilename();
            Path filepath = Paths.get(uploadDir + filename);
            Files.write(filepath, imageFile.getBytes());

            existing.setImage(filename);
        }

        productRepo.save(existing);

        return "redirect:/products";
    }




    /* ===========================================
       DELETE PRODUCT ---> /products/delete/{id}
    ============================================ */
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, HttpSession session) {

        User loggedUser = (User) session.getAttribute("user");
        if (loggedUser == null) return "redirect:/login";

        Product p = productRepo.findById(id).orElse(null);

        if (p != null && p.getUser().getId().equals(loggedUser.getId()))
            productRepo.delete(p);

        return "redirect:/products";
    }

    @GetMapping("/{id}")
    public String productDetails(@PathVariable Long id, Model model) {

        Product p = productRepo.findById(id).orElse(null);
        if (p == null) return "redirect:/";

        model.addAttribute("product", p);
        return "product-details";
    }

    @GetMapping("/search")
    public String search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Double min,
            @RequestParam(required = false) Double max,
            @RequestParam(required = false) Long categoryId,
            Model model
    ) {
        List<Product> results = productRepo.advancedSearch(name, city, min, max, categoryId);
        model.addAttribute("products", results);
        return "products";
    }

    @GetMapping("/products/add")
    public String addProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("cities", CITIES);
        return "products/add";
    }

    @GetMapping("/products/edit/{id}")
    public String editProduct(@PathVariable Long id, Model model) {
        Product p = productService.getById(id);

        model.addAttribute("product", p);
        model.addAttribute("cities", CITIES);
        model.addAttribute("categories", categoryService.findAll());
        return "products/edit";
    }

}
