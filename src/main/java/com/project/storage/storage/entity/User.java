package com.project.storage.storage.entity;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

// κλαση που χειριζεται αντικειμενα τυπου user -> Χρήστες
@Entity
@Table(name = "user", schema = "storage")
public class User {
    private Integer id;
    private String comments;
    private String lastname;
    private String firstname;
    private String password;
    private String username;
    private Integer enable;
    private Collection<Orders> ordersById;
    private Role roleByUserrole;
    private Store storeByStoreId;

    public User() {
    }


    public User(String comments, String lastname, String firstname, String password, String username, Integer enable, Role roleByUserrole , Store store) {

        this.comments = comments;
        this.lastname = lastname;
        this.firstname = firstname;
        this.password = password;
        this.username = username;
        this.enable = enable;
        this.roleByUserrole = roleByUserrole;
        this.storeByStoreId = store;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Basic
    @Column(name = "comments")
    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Basic
    @Column(name = "lastname")
    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    @Basic
    @Column(name = "firstname")
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    @Basic
    @Column(name = "password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Basic
    @Column(name = "username")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }



    @Basic
    @Column(name = "enable")
    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }




    @OneToMany(mappedBy = "userByUserId")
    public Collection<Orders> getOrdersById() {
        return ordersById;
    }

    public void setOrdersById(Collection<Orders> ordersById) {
        this.ordersById = ordersById;
    }

    @ManyToOne
    @JoinColumn(name = "userrole", referencedColumnName = "id", nullable = false)
    public Role getRoleByUserrole() {
        return roleByUserrole;
    }

    public void setRoleByUserrole(Role roleByUserrole) {
        this.roleByUserrole = roleByUserrole;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", referencedColumnName = "id")
    public Store getStoreByStoreId() {
        return storeByStoreId;
    }

    public void setStoreByStoreId(Store storeByStoreId) {
        this.storeByStoreId = storeByStoreId;
    }
}
