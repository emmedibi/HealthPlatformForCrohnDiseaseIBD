package com.springboot.myhealthplatform.board.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.springboot.myhealthplatform.bean.Doctor;
import com.springboot.myhealthplatform.bean.Patient;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * Classe che rappresenta l'appuntamento registrato a sistema.
 */
@Entity
@Table(name="Appointment")
public class Appointment {

	@Id
	@GeneratedValue(strategy= GenerationType.AUTO)
	private int id;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd hh:mm")
	@DateTimeFormat(pattern = "yyyy-MM-dd hh:mm")
	@Column(name="date")
	@NotNull(message = "Entity date must be provided.")
	private LocalDateTime appointmentDate;
	@Column(name="title")
	@NotEmpty(message = "Entity text must be provided.")
	@Size(min=2)
	private String text;
	// Relazione MoltiAUno con la classe ExamCategory
	@ManyToOne
	@JoinColumn(name = "examCategory_id")
	private ExamCategory examCategory;
	// Relazione MoltiAUno con la classe Patient
	@ManyToOne
	@JoinColumn(name = "patient_id")
	private Patient patient;
	// Relazione MoltiAUno con la classe Doctor
	@ManyToOne
	@JoinColumn(name = "doctor_id")
	private Doctor doctor;


	public Appointment(){}
	
/*	public Appointment(int id, LocalDateTime appointmentDate, ExamCategory examCategory, String text, Patient patient, Doctor doctor) {
		this.id = id;
		this.examCategory=examCategory;
		if(examCategory.getCategoryTitle().equals("YOUR_SPECIALIST")){
			// Set il dottore
		}
		this.appointmentDate = appointmentDate;
		this.text = text;
	}*/

	// GETTER
	
	public int getId() {
		return this.id;
	}
	public void setId(int id) {this.id = id;}

	public String getText() {
		return this.text;
	}

	
	// SETTER
	
	public void setAppointmentDate(LocalDateTime appointmentDate) {
		this.appointmentDate = appointmentDate;
	}
	
	public void setText(String text) {
		this.text = text;
	}

	public LocalDateTime getAppointmentDate() {
		return appointmentDate;
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

	public ExamCategory getExamCategory() {
		return examCategory;
	}

	public void setExamCategory(ExamCategory examCategory) {
		this.examCategory = examCategory;
	}
}
