package com.springboot.myhealthplatform.board.bean;

import java.util.Date;

/**
 * Classe astratta per i documenti presenti a sistema (PDFReport e BloodTestReport)
 */
public abstract class Report {

    private int id;

    private String description;

    private Date visitDate;

// GETTER E SETTER
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
