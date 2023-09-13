package com.springboot.myhealthplatform.bean;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
/**
 * Classe usata durante la registrazione di un nuovo utente, relativo alla classe User. I dati
 * recuperati dalla form di registrazione dell'utente vengono inseriti in un'istanza di questa classe,
 * validati e poi, per essere salvati, vengono inseriti in istanza della classe User e salvati nel database.
 */
public class UserRegistrationDto {

    @NotEmpty(message = "Entity name must be provided.")
    @Size(min=2)
    private String name;
    @NotEmpty(message = "Entity surname must be provided.")
    @Size(min=2)
    private String surname;
    @NotEmpty(message = "Entity email must be provided.")
    @Size(min=2)
    private String email;
    @NotEmpty(message = "Entity password must be provided.")
    @Size(min=2)
    private String password;
    @NotEmpty(message = "Entity username must be provided.")
    @Size(min=2)
    private String username;
    // Ruolo recuperato dalla form
    private String role;

// COSTRUTTORI

    public UserRegistrationDto() {

    }

    // GETTER E SETTER

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role){
        this.role = role;
    }

}
