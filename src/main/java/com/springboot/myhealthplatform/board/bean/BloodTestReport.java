package com.springboot.myhealthplatform.board.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.springboot.myhealthplatform.bean.Doctor;
import com.springboot.myhealthplatform.bean.Patient;
import com.springboot.myhealthplatform.board.designPattern.State;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Classe che rappresenta il referto dell'esame del sangue inserito a mano dall'utente. Eredita dalla classe
 * astratta Report.
 */
@Entity
@Table(name = "BloodTestReport")
public class BloodTestReport extends Report{

	// ATTRIBUTI //
	@Id
	@GeneratedValue(strategy= GenerationType.AUTO)
	private int id;
	@Column(name = "description")
	@NotEmpty(message = "Entity description must be provided.")
	@Size(min=2)
	private String description;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Column(name = "examDate")
	@NotNull(message = "Entity date must be provided.")
	private Date examinationDate; //visitDate

	@Column(name = "VES")
	@Min(0)
	@Max(30)
	// velocità di eritrosedimentazione (VES), misurata in mm/h
	private float ves;
	@Column(name = "CPR")
	@Min(0)
	@Max(100)
	// Proteina C reattiva (CPR), misurata in mg/L
	private float cpr;
	@Column(name = "Calprotectin")
	@Min(0)
	@Max(10000)
	// Calprotectina fecale, misurata in mg/Kg
	private float calprotectin;

	@Column(name = "platelets") // piastrine
	@Min(0)
	@Max(10000)
	// Piastrine, misurate in quantità al microlitro
	private float platelets;

	@Column(name = "neutrophils") // neutrofili
	@Min(0)
	@Max(30)
	// Neutrofili, misurati in quantità al microlitro
	private float neutrophils;

	@Column(name = "lymphocytes") // linfociti
	@Min(0)
	@Max(30)
	// Linfociti, misurati in quantità al microlitro
	private float lymphocytes;

	// RELAZIONI CON LE ALTRE ENTITA'
	// il report sugli esami del sangue può essere inserito sia dal paziente che dal medico
	// Relazione MoltiAUno con la classe Patient
	@ManyToOne
	@JoinColumn(name = "patient_id")
	private Patient patient;
	// Relazione MoltiAUno con la classe Doctor
	@ManyToOne
	@JoinColumn(name="doctor_id")
	private Doctor doctor;

	// GETTER E SETTER

	public float getVes() {
			return this.ves;
		}
	public float getCpr() {
			return this.cpr;
		}
	public float getCalprotectin() {
			return this.calprotectin;
		}

	public void setVes(float ves) {
			this.ves = ves;
		}
		
	public void setCpr(float cpr) {
			this.cpr = cpr;
		}
		
	public void setCalprotectin(float calprotectin) {
			this.calprotectin = calprotectin;
		}
	@Override
	public int getId() {
		return id;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	public float getPlatelets() {
		return platelets;
	}

	public void setPlatelets(float platelets) {
		this.platelets = platelets;
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

	public float getNeutrophils() {
		return neutrophils;
	}

	public void setNeutrophils(float neutrophils) {
		this.neutrophils = neutrophils;
	}

	public float getLymphocytes() {
		return lymphocytes;
	}

	public void setLymphocytes(float lymphocytes) {
		this.lymphocytes = lymphocytes;
	}

	public Date getExaminationDate() {
		return examinationDate;
	}

	public void setExaminationDate(Date examinationDate) {
		this.examinationDate = examinationDate;
	}

}
