package com.springboot.myhealthplatform.board.bean;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;

/**
 * Classe che rappresenta le categorie di esami che possono essere scelti durante la creazione
 * di un appuntamento. La creazione delle categorie è gestito dall'utente amministratore.
 */
@Entity
@Table(name="ExamCategories")
public class ExamCategory {

   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   private int id;

   @Column(name="categoryTitle")
   private String categoryTitle;

   //Relazione Una a Molti con la classe che definisce agli appuntamenti.
   @OneToMany(mappedBy = "examCategory", cascade = CascadeType.ALL, orphanRemoval = true)
   @JsonIgnore
   private List<Appointment> appointments;

   // COSTRUTTORI
   public ExamCategory(){}

   public ExamCategory(String categoryTitle, List<Appointment> appointments) {
      this.categoryTitle = categoryTitle;
      this.appointments = appointments;
   }

   public ExamCategory(String categoryTitle){
      this.categoryTitle = categoryTitle;
   }

   // GETTER E SETTER

   public int getId() {
      return id;
   }

   public String getCategoryTitle() {
      return categoryTitle;
   }

   public void setCategoryTitle(String categoryTitle) {
      this.categoryTitle = categoryTitle;
   }

   public List<Appointment> getAppointments() {
      return appointments;
   }

   public void setAppointments(List<Appointment> appointments) {
      this.appointments = appointments;
   }
}
