package com.pawasa.controller.manager;

import com.pawasa.FileService.StorageService;
import com.pawasa.model.Category;
import com.pawasa.model.Order;
import com.pawasa.model.Product;
import com.pawasa.repository.CategoryRepository;
import com.pawasa.repository.OrderRepository;
import com.pawasa.repository.ProductRepository;
import com.pawasa.repository.UserRepository;
import com.pawasa.service.DefaultProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Controller
public class ManagerController {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private DefaultProductService defaultProductService;

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private StorageService storageService;

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/manager")
    public String showdashboard(Model model) {
        List<Order> list = orderRepository.findAll();
        HashMap<Integer, BigDecimal> numProductperMonth = new HashMap<>();
        for (Order i : list) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(i.getOrderDate());
            if (cal.get(Calendar.YEAR) == 2022) {
                int month = cal.get(Calendar.MONTH) + 1;
                if (!numProductperMonth.containsKey(month)) {
                    numProductperMonth.put(month, i.getTotalPrice());
                } else {
                    numProductperMonth.put(month, BigDecimal.valueOf(numProductperMonth.get(month).doubleValue() + i.getTotalPrice().doubleValue()));
                }
            }
        }
        double sum = list.stream().mapToDouble(value -> value.getTotalPrice().doubleValue()).sum();
        model.addAttribute("numProduct", productRepository.findAll().size());
        model.addAttribute("numCategory", categoryRepository.findAll().size());
        model.addAttribute("numOrder", orderRepository.findAll().size());
        model.addAttribute("numCustomer", userRepository.findAll().size());
        model.addAttribute("sum", sum);
        for (Integer i : numProductperMonth.keySet()) {
            model.addAttribute("T" + i, numProductperMonth.get(i));
        }
        return "pages/manager/Manager_DashBoard";
    }

    @GetMapping("/manager/product")
    public String showProduct(@RequestParam(name = "pageIndex") Optional<Integer> pageIndex, Model model,
                              @RequestParam(name = "sort") Optional<Integer> chosen,
                              @RequestParam(name = "search") Optional<String> search) {
        int index, choose;
        if (!pageIndex.isPresent()) {
            index = 1;
        } else {
            index = pageIndex.get();
        }
        if (!chosen.isPresent()) {
            choose = 1;
        } else {
            choose = chosen.get();
            model.addAttribute("sort",choose);
        }
        PageRequest pageable;
        if (choose == 1) {
            pageable = PageRequest.of(index - 1, 12, Sort.by("productName").ascending());
        } else if (choose == 2) {
            pageable = PageRequest.of(index - 1, 12, Sort.by("productName").descending());
        } else if (choose == 3) {
            pageable = PageRequest.of(index - 1, 12, Sort.by("price").ascending());
        } else {
            pageable = PageRequest.of(index - 1, 12, Sort.by("price").descending());
        }
        Page<Product> productPage;
        if (!search.isPresent()) {
            productPage = productRepository.findAll(pageable);
        } else {
            productPage = productRepository.findByProductNameLike("%" + search.get() + "%",pageable);
            model.addAttribute("search",search.get());
        }
        int numPage = productPage.getTotalPages();
        model.addAttribute("list", productPage);
        model.addAttribute("index", numPage);
        model.addAttribute("current_index", index);
        return "pages/manager/Product";
    }

    @GetMapping("/manager/product/Edit")
    public String EditProduct(@RequestParam(name = "id") Optional<Long> id, Model model) {
        Product p = null;
        if (id.isPresent()) {
            p = productRepository.findById(id.get()).get();
        }
        model.addAttribute("product", p);
        List<Category> list_category = categoryRepository.findAll();
        model.addAttribute("list_category", list_category);
        return "pages/manager/EditProduct";
    }

    @PostMapping("/manager/product/Edit")
    public String AddProduct(@RequestParam(name = "id") Optional<Long> id,
                             @RequestParam(name = "category",defaultValue = "0") Optional<Long> categoryId,
                             @RequestParam(name = "name") Optional<String> productName,
                             @RequestParam(name = "price") Optional<Double> price,
                             @RequestParam(name = "quantity") Optional<Integer> quantity,
                             @RequestParam(name = "author") Optional<String> author,
                             @RequestParam(name = "supplier") Optional<String> supplier,
                             @RequestParam(name = "publisher") Optional<String> publisher,
                             @RequestParam(name = "publisher_year") Optional<String> publisher_year,
                             @RequestParam(name = "attrLanguage") Optional<String> attrLanguage,
                             @RequestParam(name = "attrLayout") Optional<String> attrLayout,
                             @RequestParam("isbn") Optional<String> isbn,
                             @RequestParam("discount") Optional<Integer> discount,
                             @RequestParam("file") MultipartFile file
    ) {
        Product p;
        if (!id.isPresent()) {
            p = new Product();
        } else {
            p = productRepository.findById(id.get()).get();
        }
        storageService.store(file);
        p.setCategory(categoryRepository.findCategoryById(categoryId.get()));
        p.setProductName(productName.get());
        p.setPrice(price.get());
        p.setQuantity(quantity.get());
        p.setAuthor(author.get());
        p.setSupplier(supplier.get());
        p.setPublishYear(publisher_year.get());
        p.setLanguage(attrLanguage.get());
        p.setBookLayout(attrLayout.get());
        p.setIsbn(isbn.get());
        p.setDiscount(discount.get());
        p.setImage(file.getOriginalFilename());
        p.setAvailable(true);
        defaultProductService.addProduct(p);
        return "redirect:/manager/product";
    }

    @GetMapping("/manager/product/Delete")
    public String DeleteProduct(@RequestParam(name = "id") Long id, Model model) {
        Product p = productRepository.findById(id).get();
        p.setAvailable(false);
        productRepository.save(p);
        return "redirect:/manager/product";
    }

    @GetMapping("/manager/product/Add")
    public String addProduct(Model model){
        List<Category> list_category = categoryRepository.findAll();
        model.addAttribute("list_category", list_category);
        return "pages/manager/AddProduct";
    }
}
