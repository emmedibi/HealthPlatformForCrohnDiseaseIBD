package com.springboot.myhealthplatform.board.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.springboot.myhealthplatform.bean.Patient;
import com.springboot.myhealthplatform.board.enumeration.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

/**
 * Classe che contiene tutti i dati utili al calcolo degli activity Index
 */
@Entity
@Table(name="crohnDiseaseActivityData")
public class CrohnDiseaseActivityData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd hh:mm")
    @Column(name="formDate")
    LocalDateTime formDate;
    @Column(name="weight")
    @NotNull(message = "Weight is mandatory")
    @Min(20)
    @Max(200)
    private int weight;
    @Column(name="idealBodyWeight")
    @NotNull(message = "Ideal Body Weight is mandatory")
    @Min(20)
    @Max(200)
    private int idealBodyWeight;
    @JsonIgnore
    private Sex sex;
    @Column(name="totLiquidStoolInSevenDays")
    @NotNull(message = "The liquid stool data is mandatory")
    @Min(0)
    @Max(40)
    private int totalNumberOfLiquidStoolInTheLastSevenDays;
    @Column(name="liquidStoolDayBefore")
    @NotNull(message = "The liquid stool data is mandatory")
    @Min(0)
    @Max(40)
    private int softStoolDayBefore;
    @Column(name="sevenDaysAbdominalPain")
    @NotNull(message = "Abdominal pain scale is mandatory")
    private int averagePastSevenDaysAbdominalPain;
    @Column(name="yesterdayAbdominalPain")
    @NotNull(message = "Abdominal pain scale is mandatory")
    private int yesterdayAbdominalPain;
    @Column(name="sevenDaysWellBeing")
    @NotNull(message = "Well being scale is mandatory")
    private int averagePastSevenDaysWellBeing;
    @Column(name="yesterdayWellBeing")
    @NotNull(message = "Well being scale is mandatory")
    private int yesterdayWellBeing;
    @Column(name="antiDiarrheaDrugUse")
    @NotNull(message = "Information about anti-diarrhea drug use is mandatory")
    private boolean antiDiarrheaDrugUse;
    @Column(name="abdominalMass")
    @NotNull(message = "Information about abdominal mass scale is mandatory")
    private int abdominalMass;
    @Column(name="hematocrit")
    @NotNull(message = "Hematocrit value is mandatory")
    @Min(0)
    @Max(100)
    private float hematocrit;
    @Column(name="arthralgias")
    @NotNull(message = "Arthralgias value is mandatory")
    private boolean arthralgias; // implies also arthritis
    @Column(name="uveitis")
    @NotNull(message = "Uveitis value is mandatory")
    private boolean uveitis; // iritis is a specific kind of uveitis so uveitis implies also iritis
    @Column(name="erythemaNodosum")
    @NotNull(message = "Erythema Nodosum value is mandatory")
    private boolean erythemaNodosum;
    @Column(name="pyodermaGangrenosum")
    @NotNull(message = "Pyoderma Gangrenosum value is mandatory")
    private boolean pyodermaGangrenosum;
    @Column(name="apthousStomatitis")
    @NotNull(message = "Apthous Stomatitis value is mandatory")
    private boolean apthousStomatitis; // apthous ulcers
    @Column(name="analFissure")
    @NotNull(message = "Anal Fissure value is mandatory")
    private boolean analFissure;
    @Column(name="fistula")
    @NotNull(message = "Fistula value is mandatory")
    private boolean fistula;
    @Column(name="abscess")
    @NotNull(message = "Abscess value is mandatory")
    private boolean abscess;
    @Column(name="otherFistula")
    @NotNull(message = "Other fistula value is mandatory")
    private boolean otherFistula;
    @Column(name="fever")
    @NotNull(message = "Fever value is mandatory")
    private boolean fever; // more than 37.8

    // Relazione MoltiAUno con la classe Patient
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

// COSTRUTTORE
    public CrohnDiseaseActivityData(int weight, int idealBodyWeight, Sex sex, int totalNumberOfLiquidStoolInTheLastSevenDays, int averagePastSevenDaysAbdominalPain, int yesterdayAbdominalPain, int averagePastSevenDaysWellBeing, int yesterdayWellBeing, boolean antiDiarrheaDrugUse, int abdominalMass, float hematocrit, boolean arthralgias, boolean uveitis, boolean erythemaNodosum, boolean pyodermaGangrenosum, boolean apthousStomatitis, boolean analFissure, boolean fistula, boolean abscess, boolean otherFistula, boolean fever, int softStoolDayBefore) {
        this.weight = weight;
        this.idealBodyWeight = idealBodyWeight;
        this.sex = sex;
        this.totalNumberOfLiquidStoolInTheLastSevenDays = totalNumberOfLiquidStoolInTheLastSevenDays;
        this.averagePastSevenDaysAbdominalPain = averagePastSevenDaysAbdominalPain;
        this.yesterdayAbdominalPain = yesterdayAbdominalPain;
        this.averagePastSevenDaysWellBeing = averagePastSevenDaysWellBeing;
        this.yesterdayWellBeing = yesterdayWellBeing;
        this.antiDiarrheaDrugUse = antiDiarrheaDrugUse;
        this.abdominalMass = abdominalMass;
        this.hematocrit = hematocrit;
        this.arthralgias = arthralgias;
        this.uveitis = uveitis;
        this.erythemaNodosum = erythemaNodosum;
        this.pyodermaGangrenosum = pyodermaGangrenosum;
        this.apthousStomatitis = apthousStomatitis;
        this.analFissure = analFissure;
        this.fistula = fistula;
        this.abscess = abscess;
        this.otherFistula = otherFistula;
        this.fever = fever;
        this.softStoolDayBefore = softStoolDayBefore;
    }

    // GETTER E SETTER

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getIdealBodyWeight() {
        return idealBodyWeight;
    }

    public void setIdealBodyWeight(int idealBodyWeight) {
        this.idealBodyWeight = idealBodyWeight;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public int getTotalNumberOfLiquidStoolInTheLastSevenDays() {
        return totalNumberOfLiquidStoolInTheLastSevenDays;
    }

    public void setTotalNumberOfLiquidStoolInTheLastSevenDays(int totalNumberOfLiquidStoolInTheLastSevenDays) {
        this.totalNumberOfLiquidStoolInTheLastSevenDays = totalNumberOfLiquidStoolInTheLastSevenDays;
    }

    public int getAveragePastSevenDaysAbdominalPain() {
        return averagePastSevenDaysAbdominalPain;
    }

    public void setAveragePastSevenDaysAbdominalPain(int averagePastSevenDaysAbdominalPain) {
        this.averagePastSevenDaysAbdominalPain = averagePastSevenDaysAbdominalPain;
    }

    public int getYesterdayAbdominalPain() {
        return yesterdayAbdominalPain;
    }

    public void setYesterdayAbdominalPain(int yesterdayAbdominalPain) {
        this.yesterdayAbdominalPain = yesterdayAbdominalPain;
    }

    public int getAveragePastSevenDaysWellBeing() {
        return averagePastSevenDaysWellBeing;
    }

    public void setAveragePastSevenDaysWellBeing(int averagePastSevenDaysWellBeing) {
        this.averagePastSevenDaysWellBeing = averagePastSevenDaysWellBeing;
    }

    public int getYesterdayWellBeing() {
        return yesterdayWellBeing;
    }

    public void setYesterdayWellBeing(int yesterdayWellBeing) {
        this.yesterdayWellBeing = yesterdayWellBeing;
    }

    public boolean isAntiDiarrheaDrugUse() {
        return antiDiarrheaDrugUse;
    }

    public void setAntiDiarrheaDrugUse(boolean antiDiarrheaDrugUse) {
        this.antiDiarrheaDrugUse = antiDiarrheaDrugUse;
    }

    public int getAbdominalMass() {
        return abdominalMass;
    }

    public void setAbdominalMass(int abdominalMass) {
        this.abdominalMass = abdominalMass;
    }

    public float getHematocrit() {
        return hematocrit;
    }

    public void setHematocrit(float hematocrit) {
        this.hematocrit = hematocrit;
    }

    public boolean isArthralgias() {
        return arthralgias;
    }

    public void setArthralgias(boolean arthralgias) {
        this.arthralgias = arthralgias;
    }

    public boolean isUveitis() {
        return uveitis;
    }

    public void setUveitis(boolean uveitis) {
        this.uveitis = uveitis;
    }

    public boolean isErythemaNodosum() {
        return erythemaNodosum;
    }

    public void setErythemaNodosum(boolean erythemaNodosum) {
        this.erythemaNodosum = erythemaNodosum;
    }

    public boolean isPyodermaGangrenosum() {
        return pyodermaGangrenosum;
    }

    public void setPyodermaGangrenosum(boolean pyodermaGangrenosum) {
        this.pyodermaGangrenosum = pyodermaGangrenosum;
    }

    public boolean isApthousStomatitis() {
        return apthousStomatitis;
    }

    public void setApthousStomatitis(boolean apthousStomatitis) {
        this.apthousStomatitis = apthousStomatitis;
    }

    public boolean isAnalFissure() {
        return analFissure;
    }

    public void setAnalFissure(boolean analFissure) {
        this.analFissure = analFissure;
    }

    public boolean isFistula() {
        return fistula;
    }

    public void setFistula(boolean fistula) {
        this.fistula = fistula;
    }

    public boolean isAbscess() {
        return abscess;
    }

    public void setAbscess(boolean abscess) {
        this.abscess = abscess;
    }

    public boolean isOtherFistula() {
        return otherFistula;
    }

    public void setOtherFistula(boolean otherFistula) {
        this.otherFistula = otherFistula;
    }

    public boolean isFever() {
        return fever;
    }

    public void setFever(boolean fever) {
        this.fever = fever;
    }

    public int getSoftStoolDayBefore() {
        return softStoolDayBefore;
    }

    public void setSoftStoolDayBefore(int softStoolDayBefore) {
        this.softStoolDayBefore = softStoolDayBefore;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getFormDate() {
        return formDate;
    }

    public void setFormDate(LocalDateTime formDate) {
        this.formDate = formDate;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    /**
     * Recupero tutte gli attributi di tipo Boolean da utilizzare poi
     * negli algoritmi di calcolo degli indici.
     * Non viene inserito tra questi l'attributo boolean antiDiarrheaDrugUse perché,
     * negli algoritmi, ha un peso differente rispetto agli altri attributi.
     */
    public boolean[] getAllBooleanValues(){
        boolean[] booleans = new boolean[10];
        booleans[0] = this.isAnalFissure();
        booleans[1] = this.isAbscess();
        booleans[2] = this.isArthralgias();
        booleans[3] = this.isFever();
        booleans[4] = this.isFistula();
        booleans[5] = this.isErythemaNodosum();
        booleans[6] = this.isApthousStomatitis();
        booleans[7] = this.isOtherFistula();
        booleans[8] = this.isUveitis();
        booleans[9] = this.isPyodermaGangrenosum();
        return booleans;
    }

}
