package com.project.storage.storage.entity;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.sql.Date;
import java.util.Objects;

// κλαση που χειριζεται αντικειμενα τυπου orders -> Παραγγελίες που κανουν οι αποθηκες στην κεντρικη αποθηκη
@Entity
@Table(name = "orders", schema = "storage")
public class Orders {
    private Integer id;
    private Integer quantity;
    private Date dateoforder;
    private User userByUserId;
    private Store storeByStoreId;
    private Material materialByMaterialId;
    private Integer status;
    private Integer sold;
    private Integer stock;
    private MsizeEntity msizeByMsize;



    public Orders(Integer id, Integer quantity, Date dateoforder, User userByUserId, Store storeByStoreId, Material materialByMaterialId, Integer status, Integer sold, Integer stock, MsizeEntity msizeByMsize) {
        this.id = id;
        this.quantity = quantity;
        this.dateoforder = dateoforder;
        this.userByUserId = userByUserId;
        this.storeByStoreId = storeByStoreId;
        this.materialByMaterialId = materialByMaterialId;
        this.status = status;
        this.sold = sold;
        this.stock = stock;
        this.msizeByMsize = msizeByMsize;
    }

    public Orders(){ }

    @Basic
    @Column(name = "stock")
    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    @Basic
    @Column(name = "sold")
    public Integer getSold() {
        return sold;
    }

    public void setSold(Integer sold) {
        this.sold = sold;
    }

    @Id
    @Column(name = "id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Basic
    @Column(name = "status")
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Basic
    @Column(name = "quantity")
    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Basic
    @Column(name = "dateoforder")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    public Date getDateoforder() {
        return dateoforder;
    }

    public void setDateoforder(Date dateoforder) {
        this.dateoforder = dateoforder;
    }



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    public User getUserByUserId() {
        return userByUserId;
    }

    public void setUserByUserId(User userByUserId) {
        this.userByUserId = userByUserId;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", referencedColumnName = "id", nullable = false)
    public Store getStoreByStoreId() {
        return storeByStoreId;
    }

    public void setStoreByStoreId(Store storeByStoreId) {
        this.storeByStoreId = storeByStoreId;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", referencedColumnName = "id", nullable = false)
    public Material getMaterialByMaterialId() {
        return materialByMaterialId;
    }


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "msize", referencedColumnName = "id", nullable = false)
    public MsizeEntity getMsizeByMsize() {
        return msizeByMsize;
    }

    public void setMsizeByMsize(MsizeEntity msizeByMsize) {
        this.msizeByMsize = msizeByMsize;
    }


    public void setMaterialByMaterialId(Material materialByMaterialId) {
        this.materialByMaterialId = materialByMaterialId;
    }
}
