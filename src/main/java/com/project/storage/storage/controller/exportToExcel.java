package com.project.storage.storage.controller;

import com.project.storage.storage.entity.Material;
import com.project.storage.storage.entity.Orders;
import com.project.storage.storage.entity.Store;
import com.project.storage.storage.entity.User;
import com.project.storage.storage.excel.AllMaterialsToExcel;
import com.project.storage.storage.excel.MaterialsByStoreExcel;
import com.project.storage.storage.service.MaterialService;
import com.project.storage.storage.service.MsizeService;
import com.project.storage.storage.service.OrderService;
import com.project.storage.storage.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


import java.util.List;
import java.util.stream.Collectors;


// ελεγκτης για εξαγωγη αποθεματων σε excel
@Controller
public class exportToExcel {

    private final MaterialService materialService;
    private final MsizeService msizeService;
    private final OrderService orderService;
    private final UserService userService;


    public exportToExcel(MaterialService materialService, MsizeService msizeService, OrderService orderService, UserService userService) {
        this.materialService = materialService;
        this.msizeService = msizeService;
        this.orderService = orderService;
        this.userService = userService;
    }

    public List<Material> getAllMaterials() { return materialService.findAll(); }

    public List<Orders> getAllOrders() { return orderService.findAll().stream().filter(o -> o.getStatus()==2).collect(Collectors.toList()); }

    // για την κεντρικη αποθηκη
    @PostMapping("/materials/export/all")
    public void exportToExcel(HttpServletResponse response) throws IOException {

        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=allMaterials" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        List<Material> allmats = getAllMaterials();
        String attributeDescription = "Σε Excel: ";

        AllMaterialsToExcel allMaterialsToExcel =
                new AllMaterialsToExcel(allmats) ;

        allMaterialsToExcel.export(response);


    }


   // για δευτερευσουσες αποθηκες
    @PostMapping("/materials/export/byStore")
    public void exportToExcelByStore(HttpServletResponse response) throws IOException {


        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
        String username = loggedInUser.getName();  // username χρηστη
        User user = userService.findByUsername(username);  // ευρεση του χρηστη
        Store currentStore =  user.getStoreByStoreId();

        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=materialsByStore" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        List<Orders> materialsInStore = new ArrayList<>();
        materialsInStore = getAllOrders().stream().filter( ord -> (ord.getStoreByStoreId().getId() == currentStore.getId())).collect(Collectors.toList());


        materialsInStore.sort(Comparator.nullsLast(
                Comparator.comparing(mat -> mat.getMaterialByMaterialId().getText() )
        ));

        String attributeDescription = "Σε Excel: ";

        MaterialsByStoreExcel materialsByStoreExcel =
                new MaterialsByStoreExcel(materialsInStore , user.getStoreByStoreId()) ;

        materialsByStoreExcel.export(response);


    }
}
