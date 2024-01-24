package com.project.storage.storage.controller;

import com.project.storage.storage.entity.Store;
import com.project.storage.storage.entity.User;
import com.project.storage.storage.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class MainController {


    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private final UserService userService;
    private final UserRoleService userRoleService;
    private final StoreService storeService;
    private final OrderService orderService;
    private final MaterialService materialService;



    public MainController(UserService userService, UserRoleService userRoleService, StoreService storeService, OrderService orderService, MaterialService materialService) {
        this.userService = userService;
        this.userRoleService = userRoleService;
        this.storeService = storeService;
        this.orderService = orderService;
        this.materialService = materialService;
    }


    @GetMapping("/login")
    public String login() {
        return "login";
    }


    @GetMapping("/index")
    public String index(Model model) {
        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
        String username = loggedInUser.getName();  // username χρηστη
        User user = userService.findByUsername(username);  // ευρεση του χρηστη
        Store store = user.getStoreByStoreId();
        String role = loggedInUser.getAuthorities().toString();  // ρολος χρηστη
         // πληθος ενεργων χρηστων
        long userCount = userService.findAll().stream().filter(u -> u.getEnable()==1).count();

        // πληθος ενεργων αποθηκων
        long storageCount = storeService.findAll().stream().filter(s -> s.getEnable()==1 && s.getId() != 1).count();

        // πληθος ειδών
        long materialCount = materialService.findAll().stream().count();


        // πληθος νεων παραγγελιών
        long newOrdersCount = 0;
        // πληθος Σύνολο  παραγγελιών
        long ordersCount = 0;
        if (role.equals("[SuperAdmin]")) { // "SuperAdmin"

            newOrdersCount = orderService.findAll().stream().filter(o -> o.getStatus() == 0).count();
            ordersCount = orderService.findAll().size();
        }
        else   if (role.equals("[LocalAdmin]")) {

            newOrdersCount =  orderService.findAll().stream().filter(o -> o.getStoreByStoreId().getId().equals(user.getStoreByStoreId().getId()) &&  o.getStatus() == 0).count() ;

            ordersCount =  orderService.findAll().stream().filter(o -> o.getStoreByStoreId().getId().equals(user.getStoreByStoreId().getId())).count() ;

        }


        model.addAttribute("user", user);
        model.addAttribute("userCount", userCount);
        model.addAttribute("storageCount", storageCount);
        model.addAttribute("materialCount", materialCount);
        model.addAttribute("newOrdersCount", newOrdersCount);
        model.addAttribute("ordersCount", ordersCount);
        model.addAttribute("store", store);
        
        return "index";
    }

    @GetMapping("/error")
    public String error() {

        return "error";
    }



    @GetMapping("/")
    public String any() {
        return "redirect:/index";
    }
}
