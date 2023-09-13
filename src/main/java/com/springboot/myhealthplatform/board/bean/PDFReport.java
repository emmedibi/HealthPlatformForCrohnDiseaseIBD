package com.springboot.myhealthplatform.board.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.springboot.myhealthplatform.bean.Doctor;
import com.springboot.myhealthplatform.bean.Patient;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Classe che rappresenta i file PDF caricati e scaribili dall'applicativo. Eredita dalla classe
 * astratta Report.
 */
@Entity
@Table(name = "pdfReport")
public class PDFReport extends Report{

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;
    @Column(name = "description")
    @NotEmpty(message = "Entity description must be provided.")
    @Size(min=2)
    private String description;

    @Column(name = "type")
    private String type; //mime type (.pdf)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "examDate")
    private Date visitDate;

    @Column(name = "file", columnDefinition="BLOB")
    @Lob
    private byte[] data;

    // Relazione MoltiAUno con la classe Patient
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    // Relazione MoltiAUno con la classe Doctor
    @ManyToOne
    @JoinColumn(name="doctor_id")
    private Doctor doctor;

// COSTRUTTORI
    public PDFReport() {};

    // costruttore usato quando il PDF viene caricato dal paziente //
    public PDFReport(String description, String type, byte[] data, Patient patient){
        this.description = description;
        this.type = type;
        this.data = data;
        this.patient = patient;
    }

    // costruttore usato quando il PDF viene caricato dal medico //
    public PDFReport(String description, String type, byte[] data, Patient patient, Doctor doctor){
        this.description = description;
        this.type = type;
        this.data = data;
        this.patient = patient;
        this.doctor = doctor;
    }

    // GETTER E SETTER
    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Date getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(Date visitDate) {
        this.visitDate = visitDate;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

}
