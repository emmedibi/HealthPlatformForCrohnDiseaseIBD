package com.springboot.myhealthplatform.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.springboot.myhealthplatform.board.bean.Message;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Collection;
import java.util.List;

/**
 * Classe utente: tramite questa classe vengono rappresentati sia l'admin sia l'infermiere.
 * Per i medici e i pazienti, la classe User registra le informazioni base e solo nella classe User
 * vengono tenute le informazioni di username, password e email.
 */
@Entity
@Table(name = "User")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(name = "name")
  @NotEmpty(message = "Entity name must be provided.")
  @Size(min=2)
  private String name;

  @Column(name = "surname")
  @NotEmpty(message = "Entity surname must be provided.")
  @Size(min=2)
  private String surname;

  @Column(unique = true, name = "email")
  @NotEmpty(message = "Entity email must be provided.")
  @Size(min=2)
  private String email;

  @Column(nullable = false, unique = true, name = "username")
  @NotEmpty(message = "Entity username must be provided.")
  @Size(min=2)
  private String username;
  @Column(nullable = false, name = "password")
  @NotEmpty(message = "Entity password must be provided.")
  @Size(min=2)
  private String password;

  // Relazione MoltiAMolti con la classe Role.
  @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @JoinTable( name= "user_role",
              joinColumns = @JoinColumn(name = "user_id"),
              inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Collection<Role> roles;

  // Relazione UnoAMolti con la classe Message. Rappresenta il mittente del messaggio.
  @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  private List<Message> sendMessages;

  // Relazione UnoAMolti con la classe Message. Rappresenta il destinatario del messaggio.
@OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  private List<Message> receivedMessages;

// COSTRUTTORI
  public User(String username, String password) {
    this.username = username;
    this.password = password;
  }

  public User() {

  }

  public User(String username, String name, String surname, String email, String password,
              Collection<Role> roles) {
    this.username = username;
    this.name = name;
    this.surname = surname;
    this.email = email;
    this.password = password;
    this.roles = roles;
  }

  // GETTER E SETTER
  public Long getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public String getName() {
    return name;
  }

  public String getSurname() {
    return surname;
  }

  public String getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }

  public Collection<Role> getRoles() {
    return roles;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setSurname(String surname) {
    this.surname = surname;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setRoles(Collection<Role> roles) {
    this.roles = roles;
  }

/*  public String toString() {
    return "User {" +
        "id= " + id +
        "username= " + username +
        "password= " + password;
  }*/

}
