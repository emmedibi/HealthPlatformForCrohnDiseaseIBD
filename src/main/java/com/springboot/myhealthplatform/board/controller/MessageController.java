package com.springboot.myhealthplatform.board.controller;

import com.springboot.myhealthplatform.bean.Doctor;
import com.springboot.myhealthplatform.bean.Patient;
import com.springboot.myhealthplatform.bean.User;
import com.springboot.myhealthplatform.board.bean.DiaryEntry;
import com.springboot.myhealthplatform.board.bean.Message;
import com.springboot.myhealthplatform.board.service.DiaryEntryService;
import com.springboot.myhealthplatform.board.service.MessageService;
import com.springboot.myhealthplatform.login.CustomUserDetailsService;
import com.springboot.myhealthplatform.service.CustomDoctorDetailService;
import com.springboot.myhealthplatform.service.CustomPatientDetailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 * Classe Controller riceve gli user input relativi agli appuntamenti (classe Message),
 * li elabora e invia la richiesta al Service opportuno; allo stesso tempo riceve le informazioni
 * richieste dal Service e le elabora e le invia all'interfaccia utente.
 */
@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;
    @Autowired
    private CustomPatientDetailService customPatientDetailService;
    @Autowired
    private CustomDoctorDetailService customDoctorDetailService;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private DiaryEntryService diaryEntryService;

    /** Costruttore della classe MessageController
     *
     * @param messageService
     * @param customPatientDetailService
     * @param customDoctorDetailService
     * @param customUserDetailsService
     * @param diaryEntryService
     */
    public MessageController(MessageService messageService, CustomPatientDetailService customPatientDetailService, CustomDoctorDetailService customDoctorDetailService, CustomUserDetailsService customUserDetailsService, DiaryEntryService diaryEntryService) {
        this.messageService = messageService;
        this.customPatientDetailService = customPatientDetailService;
        this.customDoctorDetailService = customDoctorDetailService;
        this.customUserDetailsService = customUserDetailsService;
        this.diaryEntryService = diaryEntryService;
    }


    /**
     * Recupera i messaggi inviati e ricevuti dall'utente loggato (ROLE_PATIENT)
     * @param model modello da utilizzare per riportare le informazioni su Thymeleaf
     * @param user utente loggato
     * @return la pagina html dove poter mostrare a schermo la lista di messaggi ricevuti e inviati
     * dal paziente.
     */
    @GetMapping("message/patientBoard")
    public String showMessageToPatient(Model model, Principal user){
        try {
            // Verifico che l'utente loggato sia un paziente.
            Patient patient = customPatientDetailService.findPatientByUserUsername(user.getName());
            // Recupero tutti i messaggi del paziente.
            List<Message> messages = messageService.findAllByUserId(patient.getUser().getId());
            model.addAttribute("messages", messages);
        } catch (Exception e){
            String message = "Errore durante la ricerca dei messaggi";
            model.addAttribute("messageAlert", message);
        }
        return "/message/patientMessageBoard.html";
    }

    /**
     * Recupera tutti i messaggi indirizzati al medico loggato che non sono ancora stati letti.
     * @param model modello da utilizzare per riportare le informazioni su Thymeleaf
     * @param user utente loggato (ROLE_DOCTOR)
     * @return la pagina dove verranno mostrati tutti i messaggi recuperati dal database.
     */
    @GetMapping("doctor/message/doctorBoard")
    public String showNotReadMessageToDoctor(Model model, Principal user){
        try {
            // Verifico che l'utente loggato sia un medico.
            Doctor doctor = customDoctorDetailService.findDoctorByUsername(user.getName());
            // Recupero tutti i messaggi del medico.
            List<Message> messages = messageService.findAllNotReadMessagesByUserId(doctor.getUser().getId());
            model.addAttribute("messages", messages);
        } catch (Exception e){
            String message = e.getMessage();
            model.addAttribute("message", message);
            return "/myErrorPage.html";
        }
        return "/doctor/message/doctorMessageBoard.html";
    }

    /**
     * Recupera i messaggi di uno specifico paziente che non sono ancora letti.
     * @param model modello da utilizzare per riportare le informazioni su Thymeleaf
     * @param patientId utente selezionato di cui si vuole leggere i messaggi
     * @param user utente loggato (ROLE_DOCTOR)
     * @return la pagina dove poter vedere i messaggi recuperati secondo i criteri indicati.
     */
    @GetMapping("doctor/message/NotReadMessagesPerPatient/{id}")
    public String showNotReadMessagesToDoctorPerPatient(Model model, @PathVariable("id") int patientId, Principal user){
        String messageAlert;
        try{
            // Recupera i dati del paziente
            Patient patient = customPatientDetailService.findPatientById(patientId);
            // Ricerca tutti i messaggi ricevuti da parte del paziente e non ancora letti
            List<Message> messages = messageService.findAllNotReadMessageSendByUserId(patient.getUser().getId());
            model.addAttribute("messages", messages);
        } catch(NullPointerException e){
            messageAlert = e.getMessage();
            model.addAttribute("messages", messageAlert);
        } catch(Exception e){
            messageAlert = "Errore nel caricamento dei messaggi";
            model.addAttribute("messages", messageAlert);
        }
        return "doctor/message/NotReadMessagesPerPatient.html";
    }

    /**
     * Recupera i messaggi di uno specifico paziente che non sono ancora letti.
     * @param model modello da utilizzare per riportare le informazioni su Thymeleaf
     * @param patientId utente selezionato di cui si vuole leggere i messaggi
     * @param user utente loggato (ROLE_NURSE)
     * @return la pagina dove poter vedere i messaggi recuperati secondo i criteri indicati.
     */
    @GetMapping("nurse/message/NotReadPatientMessages/{id}")
    public String showNotReadMessagesToNurse(Model model, @PathVariable("id") int patientId, Principal user){
        String messageAlert;
        try {
            // Recupera i dati del paziente
            Patient patient = customPatientDetailService.findPatientById(patientId);
            // Ricerca tutti i messaggi ricevuti da parte del paziente e non ancora letti
            List<Message> messages = messageService.findAllNotReadMessageSendByUserId(patient.getUser().getId());
            model.addAttribute("messages", messages);
        } catch(NullPointerException e){
            messageAlert = e.getMessage();
            model.addAttribute("messages", messageAlert);
        } catch (Exception e){
            messageAlert = "Errore nel caricamento dei messaggi";
            model.addAttribute("messages", messageAlert);
        }
        return "nurse/message/NotReadPatientMessages.html";
    }

    /**
     * Recupera i dati riferiti ad un nuovo messaggio dalla form del Paziente, verifica la validità
     * dei dati e salva il messaggio nel database.
     * @param model modello da utilizzare per riportare le informazioni su Thymeleaf
     * @param user utente loggato (ROLE_PATIENT)
     * @param message oggetto di classe Message che contiene i dati recuperati dalla form.
     * @param messageBindingResult oggetto che contiene tutti gli errori di validazione riferiti
     *                             ai dati inseriti nella form
     * @return la pagina della form laddove l'esito del salvataggio va a buon fine. Se invece viene lanciata
     * qualche eccezione, viene mostrata la pagina di errore.Se l'errore è riferito ad un errore di
     * validazione, questo viene mostrato sopra la form del messaggio.
     */
    @PostMapping("message/addANewMessage")
    public String addAPatientNewMessage(Model model, Principal user, @Valid @ModelAttribute("message") Message message, BindingResult messageBindingResult){
        String messageAlert;
        List<String> errorMessagesToShow = new ArrayList<>();
        String errorFromFromData = null;
        /*RACCOLTA DEGLI ERRORI DI VALIDAZIONE
        GLI ERRORI VENGONO MOSTRATI A SCHERMO TRAMITE LA LISTA errorMessagesToShow
        CHE RACCOGLIE IL CAMPO NON VALIDO (error.getField()) E IL MESSAGGIO DI ERRORE (error.getDefaultMessage())*/
        // Se ci sono errori di validazione //
        // 1. ERRORI RIFERITI AGLI ATTRIBUTI DELLA CLASSE UserRegistrationDto
        if(messageBindingResult.hasErrors()) {
            // Per ogni errore di validazione trovato da BindingResult //
            for (ObjectError error : messageBindingResult.getAllErrors()) {
                // System.out.println(error.getDefaultMessage());
                // Recupero il campo non valido
                String fieldErrors = ((FieldError) error).getField();
                // Recupero il messaggio relativo alla mancata validità del dato
                String errorMessage = (error).getDefaultMessage();
                // Costruisco una stringa con i appena ottenuti
                errorFromFromData = "The data referred to the field " + fieldErrors + " is not valid: " + errorMessage; // Costruisco una stringa con i dati utili
                // Inserisco la stringa in una lista
                errorMessagesToShow.add(errorFromFromData);
            }
            // la lista viene inserita nel model che mostrerà poi i dati all'utente.
            model.addAttribute("errorMessages", errorMessagesToShow);
            // Si viene rindirizzati nuovamente alla pagina della form.
            return "/message/NewMessageForm.html";
        }
        try{
            // Verifica che l'utente loggato sia un paziente.
            Patient patient = customPatientDetailService.findPatientByUserUsername(user.getName());
            // Inserisco tutti i valori utili e salvo.
            message.setReadMessage(false);
            message.setSendingDate(new Date()); // data di oggi, ora dell'invio
            message.setSender(patient.getUser());
            message.setRecipient(patient.getDoctor().getUser());
            messageService.save(message);
            messageAlert = "Messaggio inviato con successo all'utente " + patient.getDoctor().getSurname() + " " + patient.getDoctor().getName();
            model.addAttribute("messageAlert", messageAlert);
        }catch(Exception e){
            messageAlert = "Messaggio non inviato. Verifica.";
            model.addAttribute("messageAlert", messageAlert);
        }
        return "/message/NewMessageForm.html";
    }

    /**
     * Riceve in ingresso l'id del messaggio e, una volta recuperato nel database, lo indica come
     * letto.
     * @param model modello da utilizzare per riportare le informazioni su Thymeleaf
     * @param messageId identificativo del messaggio
     * @param user utente loggato
     * @return la pagina dove vengono recuperati i messaggi relaviti alle attività svolte sui
     * messaggi, sia che l'operazione sia andata bene sia che debba essere mostrato un errore.
     */
    @PutMapping(value="doctor/message/checkLikeRead/{id}")
    public String updateReadValueMessage(Model model, @PathVariable("id") int messageId, Principal user) {
        String messageAlert;
            try{
                // Verifica che l'utente loggato sia un medico.
                Doctor doctor = customDoctorDetailService.findDoctorByUsername(user.getName());
                // Modifica lo stato del messaggio come letto.
                messageService.setReadMessageTrue(messageId, doctor);
                messageAlert = "Messaggio segnato come letto";
                model.addAttribute("messageAlert", messageAlert);
            } catch (Exception e){
                messageAlert = e.getMessage();
                model.addAttribute("messageAlert", messageAlert);
            }
        return "doctor/message/messageInfo";
    }

    /**
     * Recuperati in input l'identificativo del messaggio a cui il medico vuole rispondere e i dati
     * inseriti nella form per il nuovo messaggio, il sistema verifica la validità dei dati e
     * procede a salvare il nuovo messaggio, indicando come destinatario il mittente del primo
     * messaggio (cioè il paziente)
     * @param model modello da utilizzare per riportare le informazioni su Thymeleaf
     * @param messageId identificatore del messaggio a cui si vuole rispondere.
     * @param message oggetto che contiene i dati derivanti dalla form che formeranno il
     *                messaggio di risposta del medico.
     * @param messageBindingResult oggetto che contiene tutti gli errori di validazione riferiti
     *                             alla form del nuovo messaggio.
     * @param user utente loggato (ROLE_DOCTOR)
     * @return la pagina che contiene tutti i messaggi riferiti ai messaggi inviati dal medico, sia
     * che il salvataggio vada a buon fine, sia che ci sia un errore durante le operazioni.
     */
    @PostMapping(value="doctor/message/sendAMessageTo/{id}")
        public String sendANewMessageToPatient(Model model, @PathVariable("id") int messageId, @Valid @ModelAttribute("message") Message message, BindingResult messageBindingResult, Principal user){
        String messageAlert;
        List<String> errorMessagesToShow = new ArrayList<>();
        String errorFromFromData = null;
        /*RACCOLTA DEGLI ERRORI DI VALIDAZIONE
        GLI ERRORI VENGONO MOSTRATI A SCHERMO TRAMITE LA LISTA errorMessagesToShow
        CHE RACCOGLIE IL CAMPO NON VALIDO (error.getField()) E IL MESSAGGIO DI ERRORE (error.getDefaultMessage())*/
        // Se ci sono errori di validazione //
        // 1. ERRORI RIFERITI AGLI ATTRIBUTI DELLA CLASSE UserRegistrationDto
        if(messageBindingResult.hasErrors()) {
            // Per ogni errore di validazione trovato da BindingResult //
            for (ObjectError error : messageBindingResult.getAllErrors()) {
                // System.out.println(error.getDefaultMessage());
                // Recupero il campo non valido
                String fieldErrors = ((FieldError) error).getField();
                // Recupero il messaggio relativo alla mancata validità del dato
                String errorMessage = (error).getDefaultMessage();
                // Costruisco una stringa con i appena ottenuti
                errorFromFromData = "The data referred to the field " + fieldErrors + " is not valid: " + errorMessage; // Costruisco una stringa con i dati utili
                // Inserisco la stringa in una lista
                errorMessagesToShow.add(errorFromFromData);
            }
            // la lista viene inserita nel model che mostrerà poi i dati all'utente.
            model.addAttribute("errorMessages", errorMessagesToShow);
            // Si viene rindirizzati nuovamente alla pagina della form.
            return "doctor/message/messageInfo";
        }
        try {
            // Verifica che l'utente loggato sia un medico.
            Doctor doctor = customDoctorDetailService.findDoctorByUsername(user.getName());
            Message answMessage = new Message();
            answMessage.setMessageBody(message.getMessageBody());
            answMessage.setReadMessage(false);
            // Recupero il messaggio a cui il medico sta rispondendo.
            Message patientMessage = messageService.checkRecipientMessage(messageId, doctor.getUser());
            // Inserisco gli altri valori utili e salvo.
            answMessage.setRecipient(patientMessage.getSender());
            answMessage.setSender(doctor.getUser());
            answMessage.setSendingDate(new Date());
            messageService.save(answMessage);
            // Modifico l'attributo notRead del messaggio ricevuto dal paziente.
            this.updateReadValues(messageId, doctor);
            messageAlert = "Hai risposto con successo al messaggio di " + answMessage.getRecipient().getSurname() + " " + answMessage.getRecipient().getName();
        } catch (Exception e){
            messageAlert = e.getMessage();
            model.addAttribute("messageAlert", messageAlert);
        }
        return "doctor/message/messageInfo";
    }

    /**
     * Recuperati in input l'identificativo del messaggio a cui l'infermiere vuole rispondere e i dati
     * inseriti nella form per il nuovo messaggio, il sistema verifica la validità dei dati e
     * procede a salvare il nuovo messaggio, indicando come destinatario il mittente del primo
     * messaggio (cioè il paziente)
     * @param model modello da utilizzare per riportare le informazioni su Thymeleaf
     * @param messageId identificatore del messaggio a cui si vuole rispondere.
     * @param message oggetto che contiene i dati derivanti dalla form che formeranno il
     *                 messaggio di risposta dell'infermiere.
     * @param messageBindingResult oggetto che contiene tutti gli errori di validazione riferiti
     *                              alla form del nuovo messaggio.
     * @param user utente loggato (ROLE_NURSE)
     * @return la pagina che contiene tutti i messaggi riferiti ai messaggi inviati dal medico, sia
     *  che il salvataggio vada a buon fine, sia che ci sia un errore durante le operazioni.
     */
    @PostMapping(value="nurse/message/sendAMessageTo/{id}")
    public String sendANewMessageToPatientByNurse(Model model, @PathVariable("id") int messageId, @Valid @ModelAttribute("message") Message message, BindingResult messageBindingResult, Principal user){
        String messageAlert;
        List<String> errorMessagesToShow = new ArrayList<>();
        String errorFromFromData = null;
        /*RACCOLTA DEGLI ERRORI DI VALIDAZIONE
        GLI ERRORI VENGONO MOSTRATI A SCHERMO TRAMITE LA LISTA errorMessagesToShow
        CHE RACCOGLIE IL CAMPO NON VALIDO (error.getField()) E IL MESSAGGIO DI ERRORE (error.getDefaultMessage())*/
        // Se ci sono errori di validazione //
        // 1. ERRORI RIFERITI AGLI ATTRIBUTI DELLA CLASSE UserRegistrationDto
        if(messageBindingResult.hasErrors()) {
            // Per ogni errore di validazione trovato da BindingResult //
            for (ObjectError error : messageBindingResult.getAllErrors()) {
                // System.out.println(error.getDefaultMessage());
                // Recupero il campo non valido
                String fieldErrors = ((FieldError) error).getField();
                // Recupero il messaggio relativo alla mancata validità del dato
                String errorMessage = (error).getDefaultMessage();
                // Costruisco una stringa con i appena ottenuti
                errorFromFromData = "The data referred to the field " + fieldErrors + " is not valid: " + errorMessage; // Costruisco una stringa con i dati utili
                // Inserisco la stringa in una lista
                errorMessagesToShow.add(errorFromFromData);
            }
            // la lista viene inserita nel model che mostrerà poi i dati all'utente.
            model.addAttribute("errorMessages", errorMessagesToShow);
            // Si viene rindirizzati nuovamente alla pagina della form.
            return "nurse/message/messageInfo";
        }
        try {
            // Verifica che l'utente loggato sia un infermiere.
            User nurse = customUserDetailsService.findNurseByUsername(user.getName());
            message.setReadMessage(false);
            // Recupero il messaggio a cui l'infermiere sta rispondendo.
            Message patientMessage = messageService.getMessageValue(messageId);
            // chiamo il metodo che permette di modificare il valore del messaggio del paziente e
            // modificare il valore notRead in true.
            messageService.setReadMessageTrueFromNurse(messageId);
            // Inserisco tutti gli altri valori utili e salvo.
            Message answMessage = new Message();
            answMessage.setMessageBody(message.getMessageBody());
            answMessage.setRecipient(patientMessage.getSender());
            answMessage.setReadMessage(false);
            answMessage.setSender(nurse);
            answMessage.setSendingDate(new Date());
            messageService.save(answMessage);
            messageAlert = "Hai risposto con successo al messaggio di " + answMessage.getRecipient().getSurname() + " " + answMessage.getRecipient().getName();
            model.addAttribute("message", messageAlert);
        } catch (Exception e){
            messageAlert = e.getMessage();
            model.addAttribute("messageAlert", messageAlert);
        }
        return "nurse/message/messageInfo";
    }

    /**
     * Recuperati in input l'identificativo della pagina di diario a cui l'infermiere vuole rispondere
     * e i dati inseriti nella form per il nuovo messaggio, il sistema verifica la validità dei dati e
     * procede a salvare il nuovo messaggio, indicando come destinatario il mittente del primo
     * messaggio (cioè il paziente)
     * @param model modello da utilizzare per riportare le informazioni su Thymeleaf
     * @param diaryEntryId identificatore della nota del diario a cui si vuole rispondere con un
     *                     messaggio.
     * @param message oggetto che contiene i dati derivanti dalla form che formeranno il
     *                 messaggio di risposta dell'infermiere.
     * @param messageBindingResult oggetto che contiene tutti gli errori di validazione riferiti
     *                              alla form del nuovo messaggio.
     * @param user utente loggato (ROLE_NURSE)
     * @return la pagina che contiene tutti i messaggi riferiti ai messaggi inviati dal medico, sia
     *  che il salvataggio vada a buon fine, sia che ci sia un errore durante le operazioni.
     */
    @PostMapping(value="nurse/message/sendAMessageToPatient/{id}")
    public String sendANewMessageFromDiaryEntryByNurse(Model model, @PathVariable("id") int diaryEntryId, @Valid @ModelAttribute("message") Message message, BindingResult messageBindingResult, Principal user){
        String messageAlert;
        List<String> errorMessagesToShow = new ArrayList<>();
        String errorFromFromData = null;
        /*RACCOLTA DEGLI ERRORI DI VALIDAZIONE
        GLI ERRORI VENGONO MOSTRATI A SCHERMO TRAMITE LA LISTA errorMessagesToShow
        CHE RACCOGLIE IL CAMPO NON VALIDO (error.getField()) E IL MESSAGGIO DI ERRORE (error.getDefaultMessage())*/
        // Se ci sono errori di validazione //
        // 1. ERRORI RIFERITI AGLI ATTRIBUTI DELLA CLASSE UserRegistrationDto
        if(messageBindingResult.hasErrors()) {
            // Per ogni errore di validazione trovato da BindingResult //
            for (ObjectError error : messageBindingResult.getAllErrors()) {
                // System.out.println(error.getDefaultMessage());
                // Recupero il campo non valido
                String fieldErrors = ((FieldError) error).getField();
                // Recupero il messaggio relativo alla mancata validità del dato
                String errorMessage = (error).getDefaultMessage();
                // Costruisco una stringa con i appena ottenuti
                errorFromFromData = "The data referred to the field " + fieldErrors + " is not valid: " + errorMessage; // Costruisco una stringa con i dati utili
                // Inserisco la stringa in una lista
                errorMessagesToShow.add(errorFromFromData);
            }
            // la lista viene inserita nel model che mostrerà poi i dati all'utente.
            model.addAttribute("errorMessages", errorMessagesToShow);
            // Si viene rindirizzati nuovamente alla pagina della form.
            return "nurse/message/messageInfo";
        }
        try {
            // Verifico che l'utente loggato sia un infermiere
            User nurse = customUserDetailsService.findNurseByUsername(user.getName());
            // Recupero la nota di diario per poter ottenere le informazioni sul paziente.
            DiaryEntry diaryEntry = diaryEntryService.findDiaryEntryByDiaryId(diaryEntryId);
            String messageText = message.getMessageBody();
            // Inserisco tutti i dati utili nel nuovo messaggio e salvo.
            Message answMessage = new Message();
            answMessage.setMessageBody("In riferimento alla sua nota del " + diaryEntry.getDate() + " :" + messageText);
            answMessage.setReadMessage(false);
            answMessage.setRecipient(diaryEntry.getPatient().getUser());
            answMessage.setReadMessage(false);
            answMessage.setSender(nurse);
            answMessage.setSendingDate(new Date());
            messageService.save(answMessage);
            messageAlert = "Hai inviato il messaggio con successo. Il messaggio è stato inviato a " + answMessage.getRecipient().getSurname() + " " + answMessage.getRecipient().getName();
            model.addAttribute("message", messageAlert);
        } catch (Exception e){
            messageAlert = e.getMessage();
            model.addAttribute("messageAlert", messageAlert);
        }
        return "nurse/message/messageInfo";
    }

    /**
     * Recuperati in input l'identificativo del messaggio a cui il medico vuole rispondere e i dati
     * inseriti nella form per il nuovo messaggio, il sistema verifica la validità dei dati e
     * procede a salvare il nuovo messaggio, indicando come destinatario il mittente del primo
     * messaggio (cioè il paziente)
     * @param model modello da utilizzare per riportare le informazioni su Thymeleaf
     * @param diaryEntryId identificatore della nota del diario a cui si vuole rispondere con
     *                     un messaggio.
     * @param message oggetto che contiene i dati derivanti dalla form che formeranno il
     *                messaggio di risposta del medico.
     * @param messageBindingResult oggetto che contiene tutti gli errori di validazione riferiti
     *                             alla form del nuovo messaggio.
     * @param user utente loggato (ROLE_DOCTOR)
     * @return la pagina che contiene tutti i messaggi riferiti ai messaggi inviati dal medico, sia
     * che il salvataggio vada a buon fine, sia che ci sia un errore durante le operazioni.
     */
    @PostMapping(value="doctor/message/sendAMessageToPatient/{id}")
    public String sendANewMessageFromDiaryEntryByDoctor(Model model, @PathVariable("id") int diaryEntryId, @Valid @ModelAttribute("message") Message message, BindingResult messageBindingResult, Principal user){
        String messageAlert;
        List<String> errorMessagesToShow = new ArrayList<>();
        String errorFromFromData = null;
        /*RACCOLTA DEGLI ERRORI DI VALIDAZIONE
        GLI ERRORI VENGONO MOSTRATI A SCHERMO TRAMITE LA LISTA errorMessagesToShow
        CHE RACCOGLIE IL CAMPO NON VALIDO (error.getField()) E IL MESSAGGIO DI ERRORE (error.getDefaultMessage())*/
        // Se ci sono errori di validazione //
        // 1. ERRORI RIFERITI AGLI ATTRIBUTI DELLA CLASSE UserRegistrationDto
        if(messageBindingResult.hasErrors()) {
            // Per ogni errore di validazione trovato da BindingResult //
            for (ObjectError error : messageBindingResult.getAllErrors()) {
                // System.out.println(error.getDefaultMessage());
                // Recupero il campo non valido
                String fieldErrors = ((FieldError) error).getField();
                // Recupero il messaggio relativo alla mancata validità del dato
                String errorMessage = (error).getDefaultMessage();
                // Costruisco una stringa con i appena ottenuti
                errorFromFromData = "The data referred to the field " + fieldErrors + " is not valid: " + errorMessage; // Costruisco una stringa con i dati utili
                // Inserisco la stringa in una lista
                errorMessagesToShow.add(errorFromFromData);
            }
            // la lista viene inserita nel model che mostrerà poi i dati all'utente.
            model.addAttribute("errorMessages", errorMessagesToShow);
            // Si viene rindirizzati nuovamente alla pagina della form.
            return "doctor/message/messageInfo";
        }
            try {
                // Verifico che l'utente loggato sia un infermiere
                User doctor = customUserDetailsService.findDoctorByUsername(user.getName());
                // Recupero la nota di diario per poter ottenere le informazioni sul paziente.
                DiaryEntry diaryEntry = diaryEntryService.findDiaryEntryByDiaryId(diaryEntryId);
                String messageText = message.getMessageBody();
                // Inserisco tutti i dati utili nel nuovo messaggio e salvo.
                Message answMessage = new Message();
                answMessage.setMessageBody("In riferimento alla sua nota del " + diaryEntry.getDate() + " :" + messageText);
                answMessage.setReadMessage(false);
                answMessage.setRecipient(diaryEntry.getPatient().getUser());
                answMessage.setSender(doctor);
                answMessage.setSendingDate(new Date());
                messageService.save(answMessage);
                messageAlert = "Hai inviato il messaggio con successo. Il messaggio è stato inviato a " + answMessage.getRecipient().getSurname() + " " + answMessage.getRecipient().getName();
                model.addAttribute("message", messageAlert);
        } catch (Exception e){
            messageAlert = e.getMessage();
            model.addAttribute("messageAlert", messageAlert);
        }
        return "doctor/message/messageInfo";
    }

    /**
     * Riceve in ingresso l'id del messaggio e, una volta recuperato nel database, lo indica come
     * letto.
     * @param model modello da utilizzare per riportare le informazioni su Thymeleaf
     * @param messageId identificativo del messaggio
     * @param user utente loggato
     * @return la pagina dove vengono recuperati i messaggi relaviti alle attività svolte sui
     * messaggi, sia che l'operazione sia andata bene sia che debba essere mostrato un errore.
     */
    @PutMapping(value="nurse/message/checkLikeRead/{id}")
    public String updateReadValuesFromNurse(Model model, @PathVariable("id") int messageId, Principal user){
        String messageAlert;
        try{
            // Verifica che l'utente loggato sia un infermiere
            User nurse = customUserDetailsService.findNurseByUsername(user.getName());
            // cambia il valore dell'attributo notRead del messaggio del paziente.
            messageService.setReadMessageTrueFromNurse(messageId);
            messageAlert = "Messaggio contrassegnato come letto";
            model.addAttribute("messageAlert", messageAlert);
        } catch(Exception e){
            messageAlert = "Abbiamo riscontrato un errore. Riprova";
            model.addAttribute("messageAlert", messageAlert);
        }
        return "nurse/message/messageInfo";
    }

    /**
     * Operazione richiamata in fase di risposta del medico ad un messaggio di un paziente,
     * in cui il messaggio del paziente viene modificato come Letto.
     * @param messageId identificativo del messaggio del paziente.
     * @param doctor utente medico che risponde al messaggio ricevuto.
     * @throws Exception viene lanciata una eccezione se dovessero esserci problemi nell'esecuzione
     * dell'operazione.
     */
    @PutMapping()
    public void updateReadValues(int messageId, Doctor doctor) throws Exception{
        try{
            messageService.setReadMessageTrue(messageId, doctor);
        } catch (Exception e){
            throw e;
        }
    }
    /**
     * Operazione richiamata in fase di risposta dell'infermiere ad un messaggio di un paziente,
     * in cui il messaggio del paziente viene modificato come Letto.
     * @param messageId identificativo del messaggio del paziente.
     * @throws Exception viene lanciata una eccezione se dovessero esserci problemi nell'esecuzione
     * dell'operazione.
     */
    @PutMapping("nurse")
    public void updateReadValuesFromNurse(int messageId) throws Exception{
        try {
            messageService.setReadMessageTrueFromNurse(messageId);
        } catch (Exception e){
            throw e;
        }
    }

}
