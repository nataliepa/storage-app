package com.project.storage.storage.controller;


import com.project.storage.storage.entity.Material;
import com.project.storage.storage.entity.Orders;
import com.project.storage.storage.entity.Store;
import com.project.storage.storage.entity.User;
import com.project.storage.storage.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

// κατασταση παραγγελιών :

//  0 -> προς εγκριση απο τον κενρτικο διαχειριστη

// 1 -> απορριφθεισα παραγγελία

// 2 -> εγκεκριμενη παραγγελία

@Controller
public class OrderCotroller {

    private final StoreService storeService;
    private final MaterialService materialService;
    private final MsizeService msizeService;
    private final UserService userService;
    private final OrderService orderService;


    public OrderCotroller(StoreService storeService, MaterialService materialService, MsizeService msizeService, UserService userService, OrderService orderService) {
        this.storeService = storeService;
        this.materialService = materialService;
        this.msizeService = msizeService;
        this.userService = userService;
        this.orderService = orderService;
    }


    private List<Store> getallStrores() {
        return storeService.findAll();
    }

    private List<Material> getallMaterials() {
        return materialService.findAll();
    }

    private List<Orders> getallOrders() {
        return orderService.findAll();
    }

    public final static int recordsByPage = 10; // eggrafes an selida

  // προβολη νεων παραγγελιων προς τον κεντρικο διαχειριστη
    @GetMapping(value = {"/neworderByStores", "/neworderByStores/{Page}"})
    public String neworderByStores(@PathVariable(required = false) String Page,
                                   Model model, @AuthenticationPrincipal UserDetails currentUser) throws IOException {

        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
        String username = loggedInUser.getName();  // username χρηστη
        User user = userService.findByUsername(username);  // ευρεση του χρηστη
        String role = loggedInUser.getAuthorities().toString();  // ρολος χρηστη

        if (role.equals("[SuperAdmin]")) { // "SuperAdmin"


            List<Orders> allOrders = getallOrders().stream().filter(o -> o.getStatus() == 0).collect(Collectors.toList());

            List<Store> allStrores = getallStrores();
            allStrores.remove(storeService.findById(1)); // οχι η κεντρικη αποθήκη
            allStrores.sort(Comparator.comparing(Store::getTitle, Comparator.nullsLast(Comparator.naturalOrder())));

            List<Store> byPages = new ArrayList<>();
            int start = 0;
            int end = (recordsByPage);
            int currentPage = 1;
            if (Page == null) {
                Page = "1";
            }

            double paging = ((double) (allStrores.size()) / recordsByPage);
            int totalPages = (int) Math.ceil(paging);
            List<Integer> pages = new ArrayList<>();
            for (int i = 1; i <= totalPages; i++) {
                pages.add(i);
            }

            if (Page.trim() != null) {
                currentPage = parseInt(Page);
                for (int j = 1; j <= totalPages; j++) {

                    if (currentPage == j) {
                        if (paging < 1) {
                            byPages = new ArrayList<Store>(allStrores.subList(start, allStrores.size()));  // 0 -1
                        } else {
                            byPages = new ArrayList<Store>(allStrores.subList(start, end));  // 0 -1
                        }


                    } else {
                        start = end;
                        end = start + (recordsByPage);

                        if (j == totalPages - 1) {
                            end = allStrores.size();
                        }
                    }

                }

            }


            model.addAttribute("totalPages", totalPages);
            model.addAttribute("currentPage", currentPage);
            model.addAttribute("pages", pages);
            model.addAttribute("allStores", byPages);
            model.addAttribute("user", user);
            model.addAttribute("records", allOrders.size());
            model.addAttribute("allOrders", allOrders);
            return "neworderByStores";
        } else {
            return "redirect:/error";
        }
    }

  // προβολη ολων των παραγγελιων
    @GetMapping(value = {"/viewAllOrders", "/viewAllOrders/{Page}"})
    public String viewAllOrders(@PathVariable(required = false) String Page,
                                Model model, @AuthenticationPrincipal UserDetails currentUser) throws IOException {

        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
        String role = loggedInUser.getAuthorities().toString();  // ρολος χρηστη
        String username = loggedInUser.getName();  // username χρηστη
        User user = userService.findByUsername(username);  // ευρεση του χρηστη

        List<Orders> allOrders = new ArrayList<>();

        if (role.equals("[SuperAdmin]")) { // "SuperAdmin"

            allOrders = getallOrders();
        } else if (role.equals("[LocalAdmin]")) {

            allOrders = getallOrders().stream().filter(o -> o.getStoreByStoreId().getId().equals(user.getStoreByStoreId().getId())).collect(Collectors.toList());

        }

        allOrders.sort(Comparator.comparing(Orders::getDateoforder, Comparator.nullsLast(Comparator.reverseOrder())));

        List<Orders> byPages = new ArrayList<>();
        int start = 0;
        int end = (recordsByPage);
        int currentPage = 1;
        if (Page == null) {
            Page = "1";
        }

        double paging = ((double) (allOrders.size()) / recordsByPage);
        int totalPages = (int) Math.ceil(paging);
        List<Integer> pages = new ArrayList<>();
        for (int i = 1; i <= totalPages; i++) {
            pages.add(i);
        }

        if (Page.trim() != null) {
            currentPage = parseInt(Page);
            for (int j = 1; j <= totalPages; j++) {

                if (currentPage == j) {
                    if (paging < 1) {
                        byPages = new ArrayList<Orders>(allOrders.subList(start, allOrders.size()));  // 0 -1
                    } else {
                        byPages = new ArrayList<Orders>(allOrders.subList(start, end));  // 0 -1
                    }


                } else {
                    start = end;
                    end = start + (recordsByPage);

                    if (j == totalPages - 1) {
                        end = allOrders.size();
                    }
                }

            }

        }


        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("pages", pages);
        model.addAttribute("allOrders", byPages);
        model.addAttribute("user", user);
        model.addAttribute("records", allOrders.size());
        model.addAttribute("allOrders", allOrders);
        return "allOrders";


    }

     // νεα παραγγελια απο τους τοπικους διαχειριστες
    @GetMapping(value = "/orders/new")
    public String newOrder(Model model) {

        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
        String username = loggedInUser.getName();  // username χρηστη
        User user = userService.findByUsername(username);  // ευρεση του χρηστη
        String role = loggedInUser.getAuthorities().toString();  // ρολος χρηστη

        if (role.equals("[LocalAdmin]")) {

            List<Material> allMaterilas = new ArrayList<>();
            allMaterilas = materialService.findAll().stream().filter(m -> m.getQuantity() > 0).collect(Collectors.toList());
            allMaterilas.sort(Comparator.comparing(Material::getText, Comparator.nullsLast(Comparator.naturalOrder())));


            model.addAttribute("newOrderForm", new Orders());
            model.addAttribute("allMaterilas", allMaterilas);
            model.addAttribute("allMsizes", msizeService.findAll());
            model.addAttribute("user", user);
            return "newOrder";

        } else {

            return "redirect:/error";

        }
    }


    @PostMapping(value = "/orders/new")
    public String newOrder(@ModelAttribute("newOrderForm") Orders newOrderForm,
                           BindingResult bindingResult, Model model) {

        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
        String username = loggedInUser.getName();  // username χρηστη
        User user = userService.findByUsername(username);  // ευρεση του χρηστη

        Optional<Material> matchingObject = materialService.findAll().stream().
                filter(p -> p.getId().equals(newOrderForm.getMaterialByMaterialId().getId()) && p.getMsizeByMsize().getId().equals(newOrderForm.getMsizeByMsize().getId())).
                findFirst();


        if (matchingObject.isEmpty() || matchingObject.get().getQuantity() < newOrderForm.getQuantity()) {
            return "redirect:/orders/new?error";

        }


        try {
            Orders obj = getallOrders().stream().max(Comparator.comparing(Orders::getId)).orElse(null);
            Integer newId = (Integer) (obj == null ? 1 : (obj.getId() + 1));  // ευρεση του αμεσως επομενου διαθεσιμου id
            newOrderForm.setId(newId);

            Date dt = new Date();
            java.sql.Date sqlDate = new java.sql.Date(dt.getTime());

            int q = newOrderForm.getQuantity();

            newOrderForm.setDateoforder(sqlDate);
            newOrderForm.setStatus(0);
            newOrderForm.setSold(0);
            newOrderForm.setStock(q);
            newOrderForm.setStoreByStoreId(user.getStoreByStoreId());
            newOrderForm.setUserByUserId(user);


            orderService.save(newOrderForm);
            return "redirect:/viewAllOrders";
        } catch (Exception e) {
            // e.printStackTrace(System.err);
            return "redirect:/error";
        }


    }

   // απορριψη παραγγελίας
    @PostMapping(value = "/orders/deny")
    public String denyOrder(Model model, @RequestParam(value = "deny", required = true) int deny) {


        try {
            Orders ord = orderService.findById(deny);
            orderService.deny(ord);
            return "redirect:/neworderByStores";
        } catch (Exception e) {
            // e.printStackTrace(System.err);
            return "redirect:/error";
        }


    }

   // εγκριση παραγγελίας
    @PostMapping(value = "/orders/accept")
    public String acceptOrder(Model model, @RequestParam(value = "accept", required = true) int accept) {


        try {
            Orders ord = orderService.findById(accept);
            Material mat = ord.getMaterialByMaterialId();

            if (mat.getQuantity() < ord.getQuantity()) {  // το αποθεμα < απο την ζητουμενη παραγγελία

                return "redirect:/neworderByStores?error=" + ord.getId();
            }
            orderService.accept(ord);
            return "redirect:/neworderByStores";
        } catch (Exception e) {
            // e.printStackTrace(System.err);
            return "redirect:/error";
        }


    }

    // πουληθεντα τεμαχια απο παραγγλίες  για τους τοπικους διαχειριστες
    @PostMapping("/orders/sold")
    public String soldMaterial(@RequestParam("orderId") String orderId, @RequestParam(value = "sold") String sold
            , Model model) throws ParseException {


        Orders ord = orderService.findById(Integer.parseInt(orderId));

        try {
            int s = Integer.parseInt(sold);

            if (s > ord.getStock() || sold.equals("")) {
                return "redirect:/materials/materialsByStore?error=" + ord.getId();
            } else {

                ord.setStock(ord.getStock() - s); // αφαιρεση πουληθεντων απο το stock της αποθηκης
                orderService.sold(ord);
            }
            return "redirect:/materials/materialsByStore";

        } catch (NumberFormatException e) {
            //  e.printStackTrace();
            return "redirect:/materials/materialsByStore?error=" + ord.getId();
        }


    }

}
