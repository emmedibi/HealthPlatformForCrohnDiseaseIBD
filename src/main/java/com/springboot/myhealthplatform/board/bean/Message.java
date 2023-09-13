package com.springboot.myhealthplatform.board.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.springboot.myhealthplatform.bean.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Classe che rapprenseta i messaggi scambiati tra personale sanitario e il paziente
 */
@Entity
@Table(name="PlatformMessage")
public class Message {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name="date")
    private Date sendingDate;
    @Column(name="messageBody")
    @NotEmpty(message = "Entity messageBody must be provided.")
    @Size(min=2)
    private String messageBody;
    @Column(name="readMessage")
    @NotNull
    private boolean readMessage;
    // Relazione MoltiAUno con la classe User --> rappresenta il mittente del messaggio
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender; // mittente
    // Relazione MoltiAUno con la classe User --> rappresenta il destinatario del messaggio
    @ManyToOne
    @JoinColumn(name="recipient_id")
    private User recipient; // destinatario

    // COSTRUTTORI //

    public Message(){};

    public Message(Date sendingDate, String messageBody, boolean readMessage, User sender, User recipient) {
        this.sendingDate = sendingDate;
        this.messageBody = messageBody;
        this.readMessage = readMessage;
        this.sender = sender;
        this.recipient = recipient;
    }

    // GETTER E SETTER //

    public Date getSendingDate() {
        return sendingDate;
    }

    public void setSendingDate(Date sendingDate) {
        this.sendingDate = sendingDate;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public boolean isReadMessage() {
        return readMessage;
    }

    public void setReadMessage(boolean readMessage) {
        this.readMessage = readMessage;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }
    public int getId() {
        return id;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }
    public void setId(int id) { this.id = id;}
}
