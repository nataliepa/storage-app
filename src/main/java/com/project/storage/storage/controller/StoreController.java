package com.project.storage.storage.controller;

import com.project.storage.storage.entity.Store;
import com.project.storage.storage.entity.User;
import com.project.storage.storage.service.StoreService;
import com.project.storage.storage.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.lang.Integer.parseInt;

@Controller
public class StoreController {


    private final UserService userService;
    private final StoreService storeService;


    public StoreController(UserService userService, StoreService storeService) {
        this.userService = userService;
        this.storeService = storeService;
    }
    public final static int recordsByPage = 30; // eggrafes an selida
    public List<Store> getAllStores() { return storeService.findAll(); }

    // εμφανιση αποθηκων
    @GetMapping(value = {"/stores", "/stores/{Page}"})
    public String users(@PathVariable(required = false) String Page,
                        Model model, @AuthenticationPrincipal UserDetails currentUser) throws IOException {

        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
        String username = loggedInUser.getName();  // username χρηστη
        User user = userService.findByUsername(username);  // ευρεση του χρηστη
        String role = loggedInUser.getAuthorities().toString();  // ρολος χρηστη

        if (role.equals("[SuperAdmin]")) { // "SuperAdmin"

            List<Store> allStores = getAllStores();
            allStores.remove(storeService.findById(1)); // οχι η κεντρικη αποθήκη
            allStores.sort(Comparator.comparing(Store::getTitle, Comparator.nullsLast(Comparator.naturalOrder())));

            List<Store> byPages = new ArrayList<>();
            int start = 0;
            int end = (recordsByPage);
            int currentPage = 1;
            if (Page == null) {
                Page = "1";
            }

            double paging = ((double) (allStores.size()) / recordsByPage);
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
                            byPages = new ArrayList<Store>(allStores.subList(start, allStores.size()));  // 0 -1
                        } else {
                            byPages = new ArrayList<Store>(allStores.subList(start, end));  // 0 -1
                        }


                    } else {
                        start = end;
                        end = start + (recordsByPage);

                        if (j == totalPages - 1) {
                            end = allStores.size();
                        }
                    }

                }

            }


            model.addAttribute("totalPages", totalPages);
            model.addAttribute("currentPage", currentPage);
            model.addAttribute("pages", pages);
            model.addAttribute("allStores", byPages);
            model.addAttribute("user", user);
            model.addAttribute("records", allStores.size());
            return "stores";
        } else {
            return "redirect:/error";
        }
    }


    @GetMapping(value = {"/stores/edit/{id}"})
    public String edit(@PathVariable("id") String id, Model model) {

        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
        String username = loggedInUser.getName();  // username χρηστη
        User user = userService.findByUsername(username);  // ευρεση του χρηστη
        String role = loggedInUser.getAuthorities().toString();  // ρολος χρηστη

        if (role.equals("[SuperAdmin]")) { // "SuperAdmin"

            Store storeForm = null;
            try {
                storeForm = storeService.findById(Integer.parseInt(id));
            } catch (NumberFormatException e) {
                return "redirect:/stores";
            }

            if (storeForm != null) {

                model.addAttribute("storeForm", storeForm);
                model.addAttribute("user", user);
                return "editStore";

            } else {
                return "redirect:/stores";
            }
        }
        return "redirect:/stores";
    }


    @PostMapping(value = "/stores/edit/{id}")
    public String edit(@PathVariable("id") int id, @ModelAttribute("storeForm") Store storeForm,
                        Model model) {



        if (storeForm.getTitle().equals("")){
            return "redirect:/stores";
        }

        try {

            storeService.edit(id, storeForm);
            return "redirect:/stores";
        } catch (Exception e) {
           // e.printStackTrace(System.err);
            return "redirect:/stores";
        }

    }


    @GetMapping(value = "/stores/new")
    public String createUser(Model model) {

        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
        String username = loggedInUser.getName();  // username χρηστη
        User user = userService.findByUsername(username);  // ευρεση του χρηστη
        String role = loggedInUser.getAuthorities().toString();  // ρολος χρηστη

        if (role.equals("[SuperAdmin]")) { // "SuperAdmin"

            model.addAttribute("newStoreForm", new Store());
            model.addAttribute("user", user);
            return "newStore";

        } else {

            return "redirect:/error";

        }
    }


    @PostMapping(value = "/stores/new")
    public String createUser(@ModelAttribute("newStoreForm") Store newStoreForm,
                             BindingResult bindingResult, Model model) {


        if (newStoreForm.getTitle().equals("")){
            return "redirect:/stores";
        }

            try {
                Store obj = getAllStores().stream().max(Comparator.comparing(Store::getId)).orElse(null);
                Integer newId = (Integer) (obj == null ? 1 : (obj.getId() + 1));  // ευρεση του αμεσως επομενου διαθεσιμου id
                newStoreForm.setId(newId);
                newStoreForm.setEnable(1);
                storeService.save(newStoreForm);
                return "redirect:/stores";
            } catch (Exception e) {
               // e.printStackTrace(System.err);
                return "redirect:/error";
            }


    }




}
