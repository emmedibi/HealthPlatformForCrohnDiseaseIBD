package com.springboot.myhealthplatform.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.cglib.core.Local;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Classe usata durante la registrazione di un nuovo utente paziente, relativo alla classe Patient. I dati
 * recuperati dalla form di registrazione dell'utente paziente vengono inseriti in un'istanza di questa classe,
 * validati e poi, per essere salvati, vengono inseriti in istanza della classe Patient e salvati nel database.
 */
public class PatientRegistrationDto {
    @NotEmpty(message = "Entity name must be provided.")
    @Size(min=2)
    private String name;
    @NotEmpty(message = "Entity surname must be provided.")
    @Size(min=2)
    private String surname;
    private boolean active;
    // active = true - il profilo funziona
    // active = false - profilo chiuso

    @NotNull(message="Doctor Id must be provided")
    private int doctorId;

    // Istanza della classe User
    private User user;


    // ATTRIBUTI DI PATIENT
    private boolean profileCompiled = false;
    // true - profilo completo
    // false - non sono stati inseriti i dati obligatori al primo accesso
    @NotEmpty(message = "Entity telephone number must be provided.")
    @Size(min=10, max=11)
    private String telephone;
    @NotEmpty(message = "Entity address must be provided.")
    @Size(min=2)
    private String address;
    @NotEmpty(message = "Entity identity code must be provided.")
    @Size(min=16, max=16)
    private String CF;
    // DOB Date Of Birth
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd hh:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm")
    private LocalDate DOB;
    @NotEmpty(message = "Entity city of birth must be provided.")
    @Size(min=2)
    private String cityOfBirth;


    public PatientRegistrationDto() {

    }

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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isProfileCompiled() {
        return profileCompiled;
    }

    public void setProfileCompiled(boolean profileCompiled) {
        this.profileCompiled = profileCompiled;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCF() {
        return CF;
    }

    public void setCF(String CF) {
        this.CF = CF;
    }

    public LocalDate getDOB() {
        return DOB;
    }

    public void setDOB(LocalDate DOB) {
        this.DOB = DOB;
    }

    public String getCityOfBirth() {
        return cityOfBirth;
    }

    public void setCityOfBirth(String cityOfBirth) {
        this.cityOfBirth = cityOfBirth;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }
}

