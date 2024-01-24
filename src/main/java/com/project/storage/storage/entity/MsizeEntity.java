package com.project.storage.storage.entity;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

// κλαση που χειριζεται αντικειμενα τυπου msize -> Μέγεθος Ενδυματατων
@Entity
@Table(name = "msize", schema = "storage")
public class MsizeEntity {
    private Integer id;
    private String name;
    private Collection<Material> materialsById;

    @Id
    @Column(name = "id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @OneToMany(mappedBy = "msizeByMsize" , fetch = FetchType.LAZY)
    public Collection<Material> getMaterialsById() {
        return materialsById;
    }

    public void setMaterialsById(Collection<Material> materialsById) {
        this.materialsById = materialsById;
    }
}
