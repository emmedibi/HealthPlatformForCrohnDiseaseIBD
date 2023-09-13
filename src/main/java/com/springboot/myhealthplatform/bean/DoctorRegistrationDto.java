package com.springboot.myhealthplatform.bean;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

/**
 * Classe usata durante la registrazione di un nuovo utente medico, relativo alla classe Doctor. I dati
 * recuperati dalla form di registrazione dell'utente medico vengono inseriti in un'istanza di questa classe,
 * validati e poi, per essere salvati, vengono inseriti in istanza della classe Doctor e salvati nel database.
 */
public class DoctorRegistrationDto {
    @NotEmpty
    @Size(min=2)
    private String name;
    @NotEmpty
    @Size(min=2)
    private String surname;
    @NotEmpty(message = "Entity identity code must be provided.")
    @Size(min=16, max=16)
    private String CF;
    @NotEmpty(message = "Entity badge number must be provided.")
    @Size(min=7, max=10)
    private String badgeNumber;
    @NotEmpty(message = "Entity telephone number must be provided.")
    @Size(min=8, max=11)
    private String telephone;

    // Istanza della classe UserRegistrationDto
    private UserRegistrationDto user;

    // COSTRUTTORE
    public DoctorRegistrationDto() {
    }

    // GETTER E SETTER //

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

    public String getCF() {
        return CF;
    }

    public void setCF(String CF) {
        this.CF = CF;
    }

    public String getBadgeNumber() {
        return badgeNumber;
    }

    public void setBadgeNumber(String badgeNumber) {
        this.badgeNumber = badgeNumber;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public UserRegistrationDto getUser() {
        return user;
    }

    public void setUser(UserRegistrationDto user) {
        this.user = user;
    }
}
