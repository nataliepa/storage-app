package com.project.storage.storage.entity;

import javax.persistence.*;
import java.util.Objects;


// κλαση που χειριζεται αντικειμενα τυπου material -> Ενδυματα
@Entity
@Table(name = "material", schema = "storage")
public class Material {
    private Integer id;
    private String text;
    private Integer quantity;
    private MsizeEntity msizeByMsize;

    @Id
    @Column(name = "id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Basic
    @Column(name = "text")
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }



    @Basic
    @Column(name = "quantity")
    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "msize", referencedColumnName = "id", nullable = false)
    public MsizeEntity getMsizeByMsize() {
        return msizeByMsize;
    }

    public void setMsizeByMsize(MsizeEntity msizeByMsize) {
        this.msizeByMsize = msizeByMsize;
    }
}
