package com.project.storage.storage.entity;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

// κλαση που χειριζεται αντικειμενα τυπου role -> Ρόλος που εχουν οι χρηστες και διευκολυνει στην εξουσιοδοτηση
@Entity
@Table(name = "role", schema = "storage")
public class Role {
    private Integer id;
    private String name;
    private Collection<User> usersById;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role that = (Role) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @OneToMany(mappedBy = "roleByUserrole")
    public Collection<User> getUsersById() {
        return usersById;
    }

    public void setUsersById(Collection<User> usersById) {
        this.usersById = usersById;
    }
}
