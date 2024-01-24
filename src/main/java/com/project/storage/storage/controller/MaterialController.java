package com.project.storage.storage.controller;

import com.project.storage.storage.entity.*;
import com.project.storage.storage.service.MaterialService;
import com.project.storage.storage.service.MsizeService;
import com.project.storage.storage.service.OrderService;
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
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

@Controller
public class MaterialController {

    private final MaterialService materialService;
    private final MsizeService msizeService;
    private final OrderService orderService;
    private final UserService userService;

    public MaterialController(MaterialService materialService, MsizeService msizeService, OrderService orderService, UserService userService) {
        this.materialService = materialService;
        this.msizeService = msizeService;
        this.orderService = orderService;
        this.userService = userService;
    }

    public final static int recordsByPage = 30; // eggrafes an selida
    public List<Material> getAllMaterials() { return materialService.findAll(); }

    public List<MsizeEntity> getAllMsizes() { return msizeService.findAll(); }

    public List<Orders> getAllOrders() { return orderService.findAll().stream().filter(o -> o.getStatus()==2).collect(Collectors.toList()); }

   // εμφανιση όλων των ενδυματων
    @GetMapping(value = {"/materials", "/materials/{Page}"})
    public String users(@PathVariable(required = false) String Page,
                        Model model, @AuthenticationPrincipal UserDetails currentUser) throws IOException {

        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
        String username = loggedInUser.getName();  // username χρηστη
        User user = userService.findByUsername(username);  // ευρεση του χρηστη
        String role = loggedInUser.getAuthorities().toString();  // ρολος χρηστη

        if (role.equals("[SuperAdmin]")) { // "SuperAdmin"

            List<Material> allMaterials = getAllMaterials();
            allMaterials.sort(Comparator.comparing(Material::getText, Comparator.nullsLast(Comparator.naturalOrder())));

            List<Material> byPages = new ArrayList<>();
            int start = 0;
            int end = (recordsByPage);
            int currentPage = 1;
            if (Page == null) {
                Page = "1";
            }

            double paging = ((double) (allMaterials.size()) / recordsByPage);
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
                            byPages = new ArrayList<Material>(allMaterials.subList(start, allMaterials.size()));  // 0 -1
                        } else {
                            byPages = new ArrayList<Material>(allMaterials.subList(start, end));  // 0 -1
                        }


                    } else {
                        start = end;
                        end = start + (recordsByPage);

                        if (j == totalPages - 1) {
                            end = allMaterials.size();
                        }
                    }

                }

            }


            model.addAttribute("totalPages", totalPages);
            model.addAttribute("currentPage", currentPage);
            model.addAttribute("pages", pages);
            model.addAttribute("allMaterials", byPages);
            model.addAttribute("user", user);
            model.addAttribute("records", allMaterials.size());
            return "materials";
        } else {
            return "redirect:/error";
        }
    }



    @GetMapping(value = "/materials/new")
    public String createUser(Model model) {

        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
        String username = loggedInUser.getName();  // username χρηστη
        User user = userService.findByUsername(username);  // ευρεση του χρηστη
        String role = loggedInUser.getAuthorities().toString();  // ρολος χρηστη

        if (role.equals("[SuperAdmin]")) { // "SuperAdmin"

            model.addAttribute("newMaterialForm", new Material());
            model.addAttribute("allMsizes",  getAllMsizes());
            model.addAttribute("user", user);
            return "newMaterial";

        } else {

            return "redirect:/error";

        }
    }


    @PostMapping(value = "/materials/new")
    public String createUser(@ModelAttribute("newMaterialForm") Material newMaterialForm,
                             BindingResult bindingResult, Model model) {

        if (newMaterialForm.getText().equals("")){
            return "redirect:/materials";
        }

        try {
            Material obj = getAllMaterials().stream().max(Comparator.comparing(Material::getId)).orElse(null);
            Integer newId = (Integer) (obj == null ? 1 : (obj.getId() + 1));  // ευρεση του αμεσως επομενου διαθεσιμου id
            newMaterialForm.setId(newId);
            try {
                materialService.save(newMaterialForm);
            } catch (Exception e) {
                //e.printStackTrace();
                return "redirect:/error";
            }
            return "redirect:/materials";
        } catch (Exception e) {
            // e.printStackTrace(System.err);
            return "redirect:/error";
        }


    }



    @GetMapping(value = {"/materials/edit/{id}"})
    public String edit(@PathVariable("id") String id, Model model) {

        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
        String username = loggedInUser.getName();  // username χρηστη
        User user = userService.findByUsername(username);  // ευρεση του χρηστη
        String role = loggedInUser.getAuthorities().toString();  // ρολος χρηστη

        if (role.equals("[SuperAdmin]")) { // "SuperAdmin"

            Material materialForm = null;
            try {
                materialForm = materialService.findById(Integer.parseInt(id));
            } catch (NumberFormatException e) {
                return "redirect:/materials";
            }

            if (materialForm != null) {

                model.addAttribute("materialForm", materialForm);
                model.addAttribute("allMsizes",  getAllMsizes());
                model.addAttribute("user", user);
                return "editMaterial";

            } else {
                return "redirect:/materials";
            }
        }
        return "redirect:/stores";
    }


    @PostMapping(value = "/materials/edit/{id}")
    public String edit(@PathVariable("id") int id, @ModelAttribute("materialForm") Material materialForm, BindingResult bindingResult, Model model) {

        if (materialForm.getText().equals("")){
            return "redirect:/materials";
        }
        try {
            materialService.edit(id, materialForm);
            return "redirect:/materials";
        } catch (Exception e) {
            // e.printStackTrace(System.err);
            return "redirect:/materials";
        }

    }


     // προβολη συγκεκριμενου ενδυματος  απο  τον κεντρικο διαχειριστη
    @GetMapping(value = {"/materials/viewStore/{id}"})
    public String viewStore(@PathVariable("id") String id, Model model) {

        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
        String username = loggedInUser.getName();  // username χρηστη
        User user = userService.findByUsername(username);  // ευρεση του χρηστη
        String role = loggedInUser.getAuthorities().toString();  // ρολος χρηστη

        if (role.equals("[SuperAdmin]")) { // "SuperAdmin"
           int materialId = Integer.parseInt(id);
           Material mat = materialService.findById(materialId);
           List<Orders> materialsInStores = getAllOrders().stream().filter( ord -> (ord.getMaterialByMaterialId().getId()== materialId) && (ord.getStock() > 0) ).collect(Collectors.toList());


            model.addAttribute("materialsInStores",materialsInStores);
            model.addAttribute("mat", mat);
            model.addAttribute("user", user);


            return "/materialInStores";



        }
        return "redirect:/materials";
    }


    // προβολη ενδυματων σε συγκεκριμενη αποθηκη για τοπικο διαχειριστη αποθηκης
    @GetMapping(value = {"/materials/materialsByStore"})
    public String materialsByStore( Model model) {

        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(loggedInUser.getName());
        Store currentStore =  user.getStoreByStoreId();
        String role = loggedInUser.getAuthorities().toString();  // ρολος χρηστη

        if (role.equals("[LocalAdmin]")) {

            List<Orders> materialsInStore = getAllOrders().stream().filter( ord -> (ord.getStoreByStoreId().getId() == currentStore.getId()) && ( ord.getStock() >0)).collect(Collectors.toList());


            model.addAttribute("materialsInStore",materialsInStore);
            model.addAttribute("user", user);


            return "/materialsByStore";

        }
        return "redirect:/materials";
    }



}
