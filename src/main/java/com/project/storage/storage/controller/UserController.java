package com.project.storage.storage.controller;


import com.project.storage.storage.entity.*;
import com.project.storage.storage.service.StoreService;
import com.project.storage.storage.service.UserRoleService;
import com.project.storage.storage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static java.lang.Integer.parseInt;

@Controller
public class UserController {

    public final static int recordsByPage = 30; // eggrafes an selida
    private final UserService userService;
    private final UserRoleService roleService;
    private final StoreService storeService;


    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public UserController(UserService userService, UserRoleService roleService, StoreService storeService) {
        this.userService = userService;
        this.roleService = roleService;
        this.storeService = storeService;
    }

    private List<User> getAllUsers() {
        return userService.findAll();
    }


    private List<Role> getUserRole() {
        return roleService.findAll();
    }


    private List<Store> getStrores() {
        return storeService.findAll();
    }

    // εμφανιση χρηστων
    @GetMapping(value = {"/users", "/users/{Page}"})
    public String users(@PathVariable(required = false) String Page,
                        Model model, @AuthenticationPrincipal UserDetails currentUser) throws IOException {

        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
        String username = loggedInUser.getName();  // username χρηστη
        User user = userService.findByUsername(username);  // ευρεση του χρηστη
        String role = loggedInUser.getAuthorities().toString();  // ρολος χρηστη

        if (role.equals("[SuperAdmin]")) { // "SuperAdmin"

            List<User> allUsers = getAllUsers();
            allUsers.sort(Comparator.comparing(User::getLastname, Comparator.nullsLast(Comparator.naturalOrder())));

            List<User> byPages = new ArrayList<>();
            int start = 0;
            int end = (recordsByPage);
            int currentPage = 1;
            if (Page == null) {
                Page = "1";
            }

            double paging = ((double) (allUsers.size()) / recordsByPage);
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
                            byPages = new ArrayList<User>(allUsers.subList(start, allUsers.size()));  // 0 -1
                        } else {
                            byPages = new ArrayList<User>(allUsers.subList(start, end));  // 0 -1
                        }


                    } else {
                        start = end;
                        end = start + (recordsByPage);

                        if (j == totalPages - 1) {
                            end = allUsers.size();
                        }
                    }

                }

            }


            model.addAttribute("totalPages", totalPages);
            model.addAttribute("currentPage", currentPage);
            model.addAttribute("pages", pages);
            model.addAttribute("allusers", byPages);
            model.addAttribute("user", user);
            model.addAttribute("records", allUsers.size());
            return "users";
        } else {
            return "redirect:/error";
        }
    }


    @GetMapping(value = {"/users/edit/{id}"})
    public String edit(@PathVariable("id") String id, Model model) {

        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
        String username = loggedInUser.getName();  // username χρηστη
        User user = userService.findByUsername(username);  // ευρεση του χρηστη
        String role = loggedInUser.getAuthorities().toString();  // ρολος χρηστη

        if (role.equals("[SuperAdmin]")) { // "SuperAdmin"

            User userForm = null;
            try {
                userForm = userService.findById(Integer.parseInt(id));
            } catch (NumberFormatException e) {
                return "redirect:/users";
            }

            if (userForm != null) {
                List<Role> roles = getUserRole();

                List<Store> stores = getStrores();
                stores.sort(Comparator.comparing(Store::getTitle, Comparator.nullsLast(Comparator.reverseOrder())));
                model.addAttribute("userForm", userForm);
                model.addAttribute("stores", stores);
                model.addAttribute("role", roles);
                model.addAttribute("user", user);
                return "editUser";

            } else {
                return "redirect:/users";
            }
        }
        return "redirect:/users";
    }


    @PostMapping(value = "/users/edit/{id}")
    public String edit(@PathVariable("id") int id, @ModelAttribute("userForm") User userForm, @RequestParam(value = "password", required = false) String newPassword, @RequestParam(value = "checkboxVisible", required = false) String checkboxVisible, BindingResult bindingResult, Model model) {

        try {


            if (newPassword != "" && !(newPassword.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{5,}$"))) {  // εαν ο κωδικος είναι < 5 χαρακτηρες
                return "redirect:/error";
            }

            if (checkboxVisible != null) {  // ενεργοποιηση χρηστη
                userForm.setEnable(1);
            } else {  // απενεργοποιηση χρήστη
                if (id != 1) { // oxi για τον SuperAdmin
                    newPassword = alphaNumericString(12);  // τυχαιος κωδικος
                    userForm.setEnable(0);
                } else if (id == 1) {
                    userForm.setEnable(1);
                }
            }

            if(id==1){// μονο για τον SuperAdmin
                userForm.setRoleByUserrole(roleService.findById(1));
            }
            userService.edit(id, userForm, newPassword.trim());
            return "redirect:/users";
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return "redirect:/error";
        }

    }

    public static String alphaNumericString(int len) {
        String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ!@#$%^&*()";
        Random rnd = new Random();

        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        }
        return sb.toString();
    }


    // αναζητηση χρηστη
    @GetMapping(value = {"/users/search/{text}"})
    public String foundUsers(@PathVariable("text") String text, Model model) {

        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
        String username = loggedInUser.getName();  // username χρηστη
        User user = userService.findByUsername(username);  // ευρεση του χρηστη
        String role = loggedInUser.getAuthorities().toString();  // ρολος χρηστη

        if (role.equals("[SuperAdmin]")) { // "SuperAdmin"


            List<User> userFound = new ArrayList<>();
            List<User> allUsers = getAllUsers();


            userFound = userBySearching(allUsers, text.trim());


            model.addAttribute("allusers", userFound);
            model.addAttribute("records", userFound.size());
            model.addAttribute("user", user);
            return "userBySearch";

        } else {
            return "redirect:/error";

        }
    }

    public List<User> userBySearching(List<User> list, String text) {
        text = nomalizeText(text);
        List<User> userFound = new ArrayList<>();
        boolean found;
        for (int i = 0; i < list.size(); i++) {
            found = false;


            if ((list.get(i).getFirstname() != null) && (nomalizeText(list.get(i).getFirstname()).toUpperCase().contains(text.toUpperCase())) && (!found)) {
                userFound.add(list.get(i));
                found = true;
            }

            if ((list.get(i).getLastname() != null) && (nomalizeText(list.get(i).getLastname()).toUpperCase().contains(text.toUpperCase())) && (!found)) {
                userFound.add(list.get(i));
                found = true;
            }
            if ((list.get(i).getUsername() != null) && (nomalizeText(list.get(i).getUsername()).toUpperCase().contains(text.toUpperCase())) && (!found)) {
                userFound.add(list.get(i));
                found = true;
            }

        }
        return userFound;
    }

    // αφαιρεση τονων στις λεξεις
    public String nomalizeText(String textToNomalize) {
        return Normalizer.normalize(textToNomalize, Normalizer.Form.NFD).replaceAll("\\p{InCOMBINING_DIACRITICAL_MARKS}+", "");
    }


    @GetMapping(value = "/users/new")
    public String createUser(Model model) {

        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
        String username = loggedInUser.getName();  // username χρηστη
        User user = userService.findByUsername(username);  // ευρεση του χρηστη
        String role = loggedInUser.getAuthorities().toString();  // ρολος χρηστη

        if (role.equals("[SuperAdmin]")) { // "SuperAdmin"


            List<Role> roles = getUserRole();

            List<Store> stores = getStrores();
            stores.sort(Comparator.comparing(Store::getTitle, Comparator.nullsLast(Comparator.reverseOrder())));

            model.addAttribute("userNewForm", new User());
            model.addAttribute("stores", stores);
            model.addAttribute("role", roles);
            model.addAttribute("user", user);
            return "newUser";

        } else {

            return "redirect:/error";

        }
    }


    @PostMapping(value = "/users/new")
    public String createUser(@ModelAttribute("userNewForm") User userNewForm, @RequestParam(value = "newpassword") String newpassword,
                             BindingResult bindingResult, Model model) {


        if (newpassword != null && !(newpassword.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{5,}$"))) {  // εαν ο κωδικος είναι < 5 χαρακτηρες
            return "redirect:/error";
        }


        if (userNewForm.getUsername().trim().length() < 5) {
            return "redirect:/error";

        }


        // ελεγχος εαν υπαρχει ηδη το  username του χρηστη
        if (userService.findByUsername(userNewForm.getUsername().trim()) == null) {


            try {
                userNewForm.setPassword(newpassword);
                userService.save(userNewForm);
                return "redirect:/users";
            } catch (Exception e) {
                e.printStackTrace(System.err);
                return "redirect:/users?error";
            }

        } else {
            return "redirect:/users?error";
        }
    }

    // αλλαγη κωδικου απο τον ιδιο τον χρηστη
    @GetMapping("/users/change")
    public String changePass(Model model) {
        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();

        String username = loggedInUser.getName();

        User user = userService.findByUsername(username);
        if (user != null) {
            model.addAttribute("userForm", user);
            model.addAttribute("user", user);
            return "changepassword";
        } else {
            return "redirect:/error";
        }
    }

    @PostMapping(value = "/users/change/{id}")
    public String changePass(@PathVariable("id") int id, @ModelAttribute("userForm") User userForm, BindingResult bindingResult, Model model,
                             @RequestParam(value = "oldpassword", required = true) String oldpassword,
                             @RequestParam(value = "pass1", required = true) String pass1,
                             @RequestParam(value = "pass2", required = true) String pass2) {

        User found = userService.findById(id);
        String oldpass = found.getPassword();  // paliow kwdikos sthn bash
        String comparepass = passwordEncoder.encode(oldpassword); // o palios kwdikos poy dhlwse o xrhsths
        boolean matches = passwordEncoder.matches(oldpassword, oldpass);


        if (matches && (pass1.equals(pass2))) { // tairiazoyn o kvdikos ths vashs kai o kvdikos toy xrhsth kai oi 2 neoi kvdikoi einai idioi


            if (!(pass1.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{5,}$"))) {  // εαν ο κωδικος είναι < 5 χαρακτηρες
                return "redirect:/users/change?mismatched1";
            }


            if (oldpassword.equals(pass1)) {  // o neow kwdikos einai idios me to isxyonta

                return "redirect:/users/change?errorPass";
            } else {
                userForm.setPassword(pass1);
                userService.changepass(id, userForm);
                return "redirect:/logout";
            }

        } else if (!matches) {   // o palios kwdikos den einai swstos

            return "redirect:/users/change?mismatched1";
        } else {   // oi neoi kwdikoi den tairiazoyn

            return "redirect:/users/change?mismatched2";
        }

    }


   // απενεργοποιηση χρηστη
    @PostMapping(value = "/users/disable")
    public String disableUser(Model model, @RequestParam(value = "disable", required = true) int disable) {


        try {
            User found =userService.findById(disable);
            found.setPassword(alphaNumericString(12));   // θετουμε τυχαιο κωδικο
            userService.disable(found);
            return "redirect:/users";
        } catch (Exception e) {
            // e.printStackTrace(System.err);
            return "redirect:/error";
        }


    }

    // ενεργοποιηση χρηστη
    @PostMapping(value = "/users/enable")
    public String enableUser(Model model, @RequestParam(value = "enable", required = true) int enable) {


        try {
            userService.enable(userService.findById(enable));
            return "redirect:/users";
        } catch (Exception e) {
            // e.printStackTrace(System.err);
            return "redirect:/error";
        }


    }


}
