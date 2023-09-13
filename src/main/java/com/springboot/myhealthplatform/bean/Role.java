package com.springboot.myhealthplatform.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Classe rappresentativa del ruolo dell'utente, utile sia alla registrazione dell'utente, sia
 * per l'autenticazione dell'utente durante il login.
 */
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(nullable=false, unique=true, length = 20)
    private String name;

    // Relazione MoltiAMolti con la classe User
    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            },
            mappedBy = "roles")
    @JsonIgnore
    private Set<User> users = new HashSet<>();

    // COSTRUTTORE
    public Role() {

    }

    // GETTER E SETTER

    public Role(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}
