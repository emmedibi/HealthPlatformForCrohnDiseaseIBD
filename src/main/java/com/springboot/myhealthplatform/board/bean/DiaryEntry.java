package com.springboot.myhealthplatform.board.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.springboot.myhealthplatform.bean.Patient;
import com.springboot.myhealthplatform.board.designPattern.DraftState;
import com.springboot.myhealthplatform.board.designPattern.SaveState;
import com.springboot.myhealthplatform.board.designPattern.State;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Classe che rappresenta le pagine di diario compilabili dal paziente e visualizzate dal
 * personale sanitario
 */
@Entity
@Table(name="diary")
public class DiaryEntry {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;

    // Relazione MoltiAUno con la classe Patient
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @Column(name = "note") // Stringa in cui inserire le note
    @NotEmpty(message = "Entity text must be provided.")
    @Size(min=2, max=800)
    private String note;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "date") // data della nota
    private Date date;

    @Column(name = "diarrhea") // presenza/assenza di diarrea
    @NotNull
    private boolean diarrhea;

    @Column(name = "bloodStool") // presenza/assenza di sangue nelle feci
    @NotNull
    private boolean bloodStool;

    @Column(name = "abdominalPain") // presenza/assenza di dolore addominale
    @NotNull
    private boolean abdominalPain;

    @Column(name = "fatigue") // presenza/assenza di fatica
    @NotNull
    private boolean fatigue;

    @Column(name = "numberBowelMovement") // numero accessi al bagno
    @Min(0)
    @Max(60)
    private int numberOfBowelMovement;

    @Column(name = "state") // stato dello specifico DiaryEntry
    private String entryState;

    @Transient
    private State state; // Riferimento allo <state Design Pattern>

    // COSTRUTTORI

    public DiaryEntry() {}

    public DiaryEntry(Patient patient, String note, Date date, boolean diarrhea, boolean bloodStool, boolean abdominalPain, boolean fatigue, int numberOfBowelMovement, String entryState, State state) {
        this.patient = patient;
        this.note = note;
        this.date = date;
        this.diarrhea = diarrhea;
        this.bloodStool = bloodStool;
        this.abdominalPain = abdominalPain;
        this.fatigue = fatigue;
        this.numberOfBowelMovement = numberOfBowelMovement;
        this.entryState = entryState;
        this.state = state;
    }

    // GETTER E SETTER

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isDiarrhea() {
        return diarrhea;
    }

    public void setDiarrhea(boolean diarrhea) {
        this.diarrhea = diarrhea;
    }

    public boolean isBloodStool() {
        return bloodStool;
    }

    public void setBloodStool(boolean bloodStool) {
        this.bloodStool = bloodStool;
    }

    public boolean isAbdominalPain() {
        return abdominalPain;
    }

    public void setAbdominalPain(boolean abdominalPain) {
        this.abdominalPain = abdominalPain;
    }

    public boolean isFatigue() {
        return fatigue;
    }

    public void setFatigue(boolean fatigue) {
        this.fatigue = fatigue;
    }

    public int getNumberOfBowelMovement() {
        return numberOfBowelMovement;
    }

    public void setNumberOfBowelMovement(int numberOfBowelMovement) {
        this.numberOfBowelMovement = numberOfBowelMovement;
    }

    public String getEntryState() {
        return entryState;
    }

    public void setEntryState(String entryState) {
        this.entryState = entryState;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    /**
     * Lo stato dell'oggetto EntryState viene settato quando viene recuperato dal database.
     * Viene letto il valore presente nell'attributo entryState e, a seconda della stringa indicata,
     * viene dato uno stato differente.
     */
    public void setStateFromEntryState(){
        // stato di Bozza
        if(this.entryState == "Draft"){
            this.setState(new DraftState(this));
        } else if (this.entryState == "Published"){
            // stato Pubblicato
            this.setState((new SaveState(this)));
        }
    }

    public int getId() {
        return id;
    }
    public void setId(int id) { this.id = id;}


}
