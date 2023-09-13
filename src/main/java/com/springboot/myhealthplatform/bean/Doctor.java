package com.springboot.myhealthplatform.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.springboot.myhealthplatform.board.bean.BloodTestReport;
import com.springboot.myhealthplatform.board.bean.PDFReport;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Classe che rappresenta l'utente medico. Eredita da BaseUser
 */
@Entity
@Table(name = "Doctor")
public class Doctor extends BaseUser {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    int id;
    @JsonIgnore
    @NotEmpty
    @Size(min=2)
    String name;
    @JsonIgnore
    @NotEmpty
    @Size(min=2)
    String surname;
    @JsonIgnore
//  Email non recuperata dal database --> salvata nella tabella user
    String email;
    @JsonIgnore
 // Password non recuperata dal database --> salvata nella tabella user
    String password;
    @JsonIgnore
    boolean active;
    // active = true - il profilo funziona
    // active = false - profilo chiuso
    @Column(nullable = false)
    @NotEmpty(message = "Entity telephone number must be provided.")
    @Size(min=8, max=11)
    String telephone;
    @Column(nullable = false, length = 16)
    @NotEmpty(message = "Entity identity code must be provided.")
    @Size(min=16, max=16)
    String CF;

    // badgeNumber = codice identificativo del medico - numero di matricola
    // Definito come yyyyMM(A-Z)
    @Column(nullable = false)
    @NotEmpty(message = "Entity badge number must be provided.")
    @Size(min=7, max=10)
    String badgeNumber;

    //Relazione UnoAUno con la classe User
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable( name= "doctor_user",
            joinColumns = @JoinColumn(name = "doctor_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private User user;

    // Relazione UnaAMolti con la classe Patient
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Patient> patients;

    // Relazione UnoAMolti con la classe PDFReport
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<PDFReport> pdfReports;

    // Relazione UnoAMolti con la classe BloodTestReport
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<BloodTestReport> bloodTestReports;

    // COSTRUTTORI

    public Doctor() {
    }

    public Doctor(String name, String surname, String telephone, String CF, String badgeNumber, User user) {
        this.name = name;
        this.surname = surname;
        this.telephone = telephone;
        this.CF = CF;
        this.badgeNumber = badgeNumber;
        this.user = user;
    }

    // GETTER

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getSurname() {
        return this.surname;
    }

    public String getPassword() {
        return this.password;
    }

    public String getEmail() {
        return this.email;
    }

    public boolean getActive() {
        return this.active;
    }

    public String getTelephone() {
        return this.telephone;
    }

    public String getCF() {
        return this.CF;
    }

    public String getBadgeNumber() {
        return badgeNumber;
    }

    // SETTER


    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setCF(String CF) {
        this.CF = CF;
    }

    public void setBadgeNumber(String badgeNumber) {
        this.badgeNumber = badgeNumber;
    }

    public boolean isActive() {
        return active;
    }

    public User getUser() {
        return user;
    }

    public List<Patient> getPatients() {
        return patients;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setPatients(List<Patient> patients) {
        this.patients = patients;
    }

    public List<PDFReport> getPdfReports() {
        return pdfReports;
    }

    public void setPdfReports(List<PDFReport> pdfReports) {
        this.pdfReports = pdfReports;
    }

    public List<BloodTestReport> getBloodTestReports() {
        return bloodTestReports;
    }

    public void setBloodTestReports(List<BloodTestReport> bloodTestReports) {
        this.bloodTestReports = bloodTestReports;
    }
}

