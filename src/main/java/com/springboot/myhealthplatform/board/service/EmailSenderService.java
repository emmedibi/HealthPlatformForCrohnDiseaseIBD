package com.springboot.myhealthplatform.board.service;

import com.springboot.myhealthplatform.board.bean.Appointment;
import jakarta.mail.SendFailedException;
import jakarta.mail.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Properties;
/**
 * Classe che comunica con le classi Repository per estrarre o registrare dati provenienti dai Controller.
 * Service dedicato alla gestione dell'invio delle email.
 */
@Component
public class EmailSenderService{

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Il metodo riceve in ingresso un oggetto di classe Appointment che contiene tutte le informazioni
     * utili all'invio della mail. Nel nostro caso specifico, per la DEMO, ho creato un nuovo indirizzo
     * email Google (myhealthplatform@gmail.com) e ho inserito come indirizzo a cui inviare le mail un
     * mio indirizzo personale.
     * @param appointment oggetto che contiene tutte le informazioni utili all'invio del messaggio.
     * @throws SendFailedException viene lanciato se si riscontrando degli errori nell'invio del messaggio.
     */
    public void sendEmail(Appointment appointment) throws SendFailedException {
        // Configuro le proprietà utili per l'invio.
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.googlemail.com");
        props.put("mail.from", "myhealthplatformproject@gmail.com");
        props.put("mail.smtp.starttls.enable", "true");
        Session session = Session.getInstance(props, null);

        // Configuro il messaggio e il destinatario del messaggio.
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("myhealthplatformproject@gmail.com");
        simpleMailMessage.setTo("emmedibi92@gmail.com"); // default sempre //
        String subject = "Nuovo appuntamento prenotato";
        simpleMailMessage.setSubject(subject);
        String message = "Buongiorno " + appointment.getPatient().getSurname() + " " + appointment.getPatient().getName() + ", è stato prenotato un nuovo appuntamento a suo nome con il Dottor " + appointment.getDoctor().getSurname() + " " + appointment.getDoctor().getName() + ".";
        simpleMailMessage.setText(message);
        // Invio del messaggio
        mailSender.send(simpleMailMessage);
    }



}
