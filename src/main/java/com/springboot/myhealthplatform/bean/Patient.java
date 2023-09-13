package com.springboot.myhealthplatform.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.springboot.myhealthplatform.board.bean.BloodTestReport;
import com.springboot.myhealthplatform.board.bean.CrohnDiseaseActivityData;
import com.springboot.myhealthplatform.board.bean.DiaryEntry;
import com.springboot.myhealthplatform.board.bean.PDFReport;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Classe che rappresenta l'utente paziente. Eredita da BaseUser
 */
@Entity
@Table(name = "Patient")
public class Patient extends BaseUser{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
//  Password non recuperata dal database --> salvata nella tabella user
    String password;
    @JsonIgnore
    boolean active; 
   // active = true - il profilo funziona
   // active = false - profilo chiuso

    //Relazione MoltiAUno con la classe Doctor
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    @NotNull
    private Doctor doctor;

    // Relazione UnoAMolti con la classe PDFReport
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<PDFReport> pdfReports;

    // Relazione UnoAMolti con la classe BloodTestReport
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<BloodTestReport> bloodTestReports;

    // Relazione UnoAMolti con la classe DiaryEntry
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<DiaryEntry> diaryEntries;
    // Relazione UnoAMolti con la classe CrohnDiseaseActivityData
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<CrohnDiseaseActivityData> crohnDiseaseActivityDataList;
    //Relazione UnoAUno con la classe User
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
   @JoinTable( name= "patient_user",
            joinColumns = @JoinColumn(name = "patient_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    @NotNull
   private User user;


   // ATTRIBUTI SPECIFICI DI PATIENT
    boolean profileCompiled = false;
   // true - profilo completo
   // false - non sono stati inseriti i dati obligatori al primo accesso
   @NotEmpty(message = "Entity telephone number must be provided.")
   @Size(min=10, max=11)
    String telephone;
    @NotEmpty(message = "Entity address must be provided.")
    @Size(min=2)
    String address;
    @NotEmpty(message = "Entity identity code must be provided.")
    @Size(min=16, max=16)
    String CF;
    // DOB Date Of Birth
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate DOB;
    @NotEmpty(message = "Entity city of birth must be provided.")
    @Size(min=2)
    String cityOfBirth;

    // Anamnesi del paziente.
    // Non implementata: andrebbe aggiunta una relazione OneToOne con una classe
    // dedicata MedicalHistory
    int medicalHistoryId;

    // COSTRUTTORI

    /**
     * Costruttore di Patient con gli attributi implementati dell'applicazione.
     * @param name nome del paziente
     * @param surname cognome del paziente
     * @param telephone telefono del paziente
     * @param CF codice fiscale
     * @param DOB data di nascita
     * @param address indirizzo di residenza
     * @param cityOfBirth città di nascita
     * @param doctor Dottore di riferimento nella struttura
     * @param user User collegato all'oggetto Patient che stiamo creando
     */
    public Patient(String name, String surname, String telephone, String CF, LocalDate DOB, String address, String cityOfBirth, Doctor doctor, User user){
        this.name = name;
        this.surname = surname;
        this.telephone = telephone;
        this.CF = CF;
        this.DOB = DOB;
        this.address = address;
        this.cityOfBirth = cityOfBirth;
        this.doctor = doctor;
        this.user = user;
    }

    /**
     * Costruttore
     */
    public Patient() {}

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

    public String getEmail() {
        return this.email;
    }

    public boolean getActive() {
        return this.active;
    }

    public boolean getProfileCompiled() {
        return this.profileCompiled;
    }

    public String getTelephone() {
        return this.telephone;
    }

    public String getAddress() {
        return this.address;
    }

    public String getCF() {
        return this.CF;
    }

    public LocalDate getDOB() {
        return this.DOB;
    }

    public String getCityOfBirth() {
        return this.cityOfBirth;
    }

    public int getMedicalHistoryId() {
        return this.medicalHistoryId;
    }

    // SETTER


    public void setId(int id){
        this.id = id;
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
    
    public void setActive(boolean active){
        this.active = active;
    }

    public void setProfileCompiled(boolean profileCompiled) {
        this.profileCompiled = profileCompiled;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCF(String CF) {
        this.CF = CF;
    }

    public void setDOB(LocalDate DOB) {
        this.DOB = DOB;
    }
    
    public void setCityOfBirth(String cityOfBirth) {
        this.cityOfBirth = cityOfBirth;
    }

    public void setMedicalHistoryId(int anamnesiId) {
        this.medicalHistoryId = medicalHistoryId;
    }

    public boolean isActive() {
        return active;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public User getUser() {
        return user;
    }

    public boolean isProfileCompiled() {
        return profileCompiled;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public void setUser(User user) {
        this.user = user;
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
