package com.springboot.myhealthplatform.board.controller;

import com.springboot.myhealthplatform.bean.Doctor;
import com.springboot.myhealthplatform.bean.Patient;
import com.springboot.myhealthplatform.board.bean.Appointment;
import com.springboot.myhealthplatform.board.bean.ExamCategory;
import com.springboot.myhealthplatform.board.service.AppointmentService;
import com.springboot.myhealthplatform.board.service.EmailSenderService;
import com.springboot.myhealthplatform.board.service.ExamCategoryService;
import com.springboot.myhealthplatform.service.CustomDoctorDetailService;
import com.springboot.myhealthplatform.service.CustomPatientDetailService;
import jakarta.mail.SendFailedException;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Classe Controller riceve gli user input relativi agli appuntamenti (classe Appointment),
 * li elabora e invia la richiesta al Service opportuno; allo stesso tempo riceve le informazioni
 * richieste dal Service e le elabora e le invia all'interfaccia utente.
 */
@Controller
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private CustomDoctorDetailService customDoctorDetailService;
    @Autowired
    private CustomPatientDetailService customPatientDetailService;
    @Autowired
    private ExamCategoryService examCategoryService;
    @Autowired
    private EmailSenderService emailSenderService;

    // COSTRUTTORI

    public AppointmentController(AppointmentService appointmentService, CustomDoctorDetailService customDoctorDetailService, CustomPatientDetailService customPatientDetailService, ExamCategoryService examCategoryService, EmailSenderService emailSenderService) {
        this.appointmentService = appointmentService;
        this.customDoctorDetailService = customDoctorDetailService;
        this.customPatientDetailService = customPatientDetailService;
        this.examCategoryService = examCategoryService;
        this.emailSenderService = emailSenderService;
    }

    public AppointmentController() {

    }

    /** Restituisce l'elenco di tutti gli appuntamenti del medico loggato. Gli appuntamenti devono
     * avvenire nel futuro (filtro e controllo che avviene nel layer Service)
     * Mostra gli appuntamenti riferiti al dottore loggato
     * @param model modello da utilizzare per riportare le informazioni su Thymeleaf
     * @param user utente loggato
     * @return la pagina URL dove verranno mostrati gli appuntamenti.
     */
    @GetMapping("doctorAgenda/doctorAgenda")
    public String showDoctorAppointments(Model model, Principal user){

        try {
            //controlla che l'utente che ha accesso abbia ruolo medico//
            Doctor doctor = customDoctorDetailService.findDoctorByUsername(user.getName());
/*            estrae tutti gli appuntamenti in base all'id del medico, poi salva gli appuntamenti nel modello utile
            alla visualizzazione front end*/
            List<Appointment> appointments = appointmentService.getAllAppointmentByDoctorId(doctor.getId());
            model.addAttribute("appointments", appointments);
        } catch(NullPointerException e){
            // Cattura l'eccezione se l'utente non è medico
            String message = e.getMessage();
            model.addAttribute("message", message);
            return "/myErrorPage.html";
        } catch(Exception e){
            // Cattura l'eccezione se lato database c'è un errore
            String message = e.getMessage();
            model.addAttribute("message", message);
            return "/myErrorPage.html";
        }
        // indirizzo pagina che verrà visualizzata
        return "/doctor/doctorAgenda/doctorAppointmentList.html";
    }

    /**
     * Recupera solo gli appuntamenti di oggi per il medico loggato.
     * @param model modello da utilizzare per riportare le informazioni su Thymeleaf
     * @param user utente loggato
     * @return la pagina URL dove verranno mostrati gli appuntamenti di oggi.
     */
    @GetMapping("doctorAgenda/doctorTodayAgenda")
    public String showDoctorTodayAppointments(Model model, Principal user){
        try {
            //controlla che l'utente che ha accesso abbia ruolo medico//
            Doctor doctor = customDoctorDetailService.findDoctorByUsername(user.getName());
            /*            estrae tutti gli appuntamenti DI OGGI in base all'id del medico, poi salva gli appuntamenti nel modello utile
            alla visualizzazione front end*/
            List<Appointment> appointments = appointmentService.getAllTodayAppointmentsByDoctorId(doctor.getId());
            model.addAttribute("appointments", appointments);
        } catch(NullPointerException e){
            // Cattura l'eccezione se l'utente non è medico
            String message = e.getMessage();
            model.addAttribute("message", message);
            // indirizzo pagina che verrà visualizzata
            return "/myErrorPage.html";
        } catch (Exception e){
            // Cattura l'eccezione se lato database c'è un errore
            String message = e.getMessage();
            model.addAttribute("message", message);
            // indirizzo pagina che verrà visualizzata
            return "/myErrorPage.html";
        }
        // indirizzo pagina che verrà visualizzata
        return "/doctor/doctorAgenda/doctorAppointmentList.html";
    }

    /**
     * Il metodo recupera la lista di pazienti del medico, in modo che vengano proposti al momento
     * della creazione dell'appuntamento.
     * @param model modello da utilizzare per riportare le informazioni su Thymeleaf
     * @param user utente loggato
     * @return la form per la creazione dell'appuntamento da parte del medico.
     */
    @GetMapping("doctorAgenda/doctorAppointmentForm")
    public String showDoctorAppointmentForm(Model model, Principal user){

        try {
            //controlla che l'utente che ha accesso abbia ruolo medico//
            Doctor doctor = customDoctorDetailService.findDoctorByUsername(user.getName());
            // Estrae l'elenco dei pazienti del medico
            List<Patient> patients = doctor.getPatients();
            model.addAttribute("patients", patients);
        } catch(NullPointerException e){
            // Cattura l'eccezione se l'utente non è medico
            String message = e.getMessage();
            model.addAttribute("message", message);
            // indirizzo pagina che verrà visualizzata
            return "/myErrorPage.html";
        } catch (Exception e){
            String message = e.getMessage();
            model.addAttribute("message", message);
            // indirizzo pagina che verrà visualizzata
            return "/myErrorPage.html";
        }
        // indirizzo pagina che verrà visualizzata
        return "/doctor/doctorAgenda/doctorAppointmentForm.html";
    }

    /**
     * Recupero degli appuntamenti del paziente e dei dati utili alla creazione di un nuovo appuntamento
     * come le categorie di esame, che è un dato da inserire durante la creazione di un nuovo appuntamento.
     * @param model modello da utilizzare per riportare le informazioni su Thymeleaf
     * @param user utente loggato. Deve essere di classe Patient.
     * @return la pagina html dove verranno mostrati gli appuntamenti del paziente e la form per la
     * creazione di un nuovo appuntamento.
     */
    @GetMapping("agenda/patientAppointments")
    public String showPatientAppointments(Model model, Principal user){
        try {
            //controlla che l'utente che ha accesso abbia ruolo paziente//
            Patient patient = customPatientDetailService.findPatientByUserUsername(user.getName());
            // Recupera gli appuntamenti del paziente e le categorie //
            List<Appointment> appointments = appointmentService.getAllAppointmentsByPatientId(patient.getId());
            List<ExamCategory> examCategories = examCategoryService.findAll();
            model.addAttribute("appointments", appointments);
            model.addAttribute("examCategories", examCategories);
        } catch (NullPointerException e){
            // Cattura l'eccezione se l'utente non è paziente
            String message = e.getMessage();
            model.addAttribute("message", message);
            // indirizzo pagina che verrà visualizzata
            return "/myErrorPage.html";
        } catch (Exception e){
            String message = "Errore durante il recupero dei dati";
            model.addAttribute("message", message);
            // indirizzo pagina che verrà visualizzata
            return "/myErrorPage.html";
        }
        // indirizzo pagina che verrà visualizzata
        return "agenda/appointmentList.html";
    }

    /**
     * Recupera gli appuntamenti del medico, da rendere visibili all'infermiera.
     * @param model modello da utilizzare per riportare le informazioni su Thymeleaf
     * @param doctorId identificativo del medico.
     * @param user utente loggato. Deve essere di classe Doctor.
     * @return pagina html che mostra gli appuntamenti del paziente per medico.
     */
    @GetMapping("nurse/agenda/DoctorAgenda/{id}")
    public String showDoctorAppointment(Model model, @PathVariable("id") int doctorId, Principal user){
        try {
            // Recupera tutti gli appuntamenti del giorno del medico selezionato //
//            List<Appointment> appointments = appointmentService.getAllAppointmentByDoctorId(doctorId);
            List<Appointment> todayAppointments = appointmentService.getAllTodayAppointmentsByDoctorId(doctorId);
            model.addAttribute("appointments", todayAppointments);
        } catch (Exception e){
            String message = "Errore durante il caricamento degli appuntamenti del medico.";
            model.addAttribute("message", message);
            // indirizzo pagina che verrà visualizzata
            return "/myErrorPage.html";
        }
        // indirizzo pagina che verrà visualizzata
        return "nurse/agenda/doctorAppointments.html";
    }

    /**
     * Ottiene la form dal front-end, verifica la validità dei dati e passa al Service i dati per
     * salvarli nel database.
     * @param model modello da utilizzare per riportare le informazioni su Thymeleaf
     * @param appointment oggetto che recupera tutti i dati utili per la creazione di un appuntamento su DB.
     * @param appointmentBindingResult oggetto che recupera gli errori di validazione riferiti ai dati provenienti dalla form
     * @param user utente loggato: DEVE ESSERE DI RUOLO PAZIENTE
     * @return ritorna alla pagina agenda/appointmentList;
     */
    @PostMapping(value="/agenda/addAnAppointment")
    public String addPatientAppointment(Model model, @Valid @ModelAttribute("appointment") Appointment appointment, BindingResult appointmentBindingResult, Principal user){
        String message;
        List<String> errorMessagesToShow = new ArrayList<>();
        String errorFromFromData = null;
        /*RACCOLTA DEGLI ERRORI DI VALIDAZIONE
        GLI ERRORI VENGONO MOSTRATI A SCHERMO TRAMITE LA LISTA errorMessagesToShow
        CHE RACCOGLIE IL CAMPO NON VALIDO (error.getField()) E IL MESSAGGIO DI ERRORE (error.getDefaultMessage())*/
        // Se ci sono errori di validazione //
        // 1. ERRORI RIFERITI AGLI ATTRIBUTI DELLA CLASSE UserRegistrationDto
        if(appointmentBindingResult.hasErrors()) {
            // Per ogni errore di validazione trovato da BindingResult //
            for (ObjectError error : appointmentBindingResult.getAllErrors()) {
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
            return "agenda/agendaMessage";
        }
        try{
            // Verifico che la data inserita nella form non sia nel passato,
            // se del passato, lancia una eccezione.
            if(appointment.getAppointmentDate().isBefore(LocalDateTime.now())){
                throw new ValidationException("The date is in the past");
            }
            // Verifico che la categoria non sia YOUR_SPECIALIST perchè il paziente
            // non può inserire questa categoria.
            if(appointment.getExamCategory().getCategoryTitle().equals(("YOUR_SPECIALIST"))){
                throw new ValidationException("La tipologia di appuntamento è riservata al medico");
            }
            // Recupero i dati del paziente, inserisco l'oggetto Patient nell'appuntamento e salvo l'appuntamento.
            Patient patient = customPatientDetailService.findPatientByUserUsername(user.getName());
            appointment.setPatient(patient);
            appointmentService.save(appointment);
            message = "Appuntamento salvato con successo.";
        } catch(ValidationException e){
            // cattura un errore di validazione.
            message = e.getMessage();
            errorMessagesToShow.add(message);
            model.addAttribute("errorMessages", errorMessagesToShow);
        } catch (Exception e){
            message = e.getMessage();
            errorMessagesToShow.add(message);
            model.addAttribute("errorMessages", errorMessagesToShow);
        }
        model.addAttribute("message", message);
        // indirizzo pagina che verrà visualizzata
        return "agenda/agendaMessage";
    }

    /**
     * Recupero dei dati della form per la creazione di un appuntamento da parte dell'utente medico.
     * @param model modello da utilizzare per riportare le informazioni su Thymeleaf
     * @param appointment oggetto che recupera tutti i dati utili per la creazione di un appuntamento su DB.
     * @param doctorAppointmentBindingResult oggetto che recupera gli errori di validazione riferiti ai dati provenienti dalla form
     * @param user utente loggato: deve essere di ruolo ROLE_DOCTOR.
     * @return ritorna la pagina "agendaMessage" dove possiamo vedere i messaggi sia riferiti ad eventuali errori di validazione
     * sia il messaggio di creazione dell'appuntamento.
     */
    @PostMapping(value="doctorAgenda/addDoctorAppointment")
    public String addDoctorAppointment(Model model, @Valid @ModelAttribute("appointment") Appointment appointment, BindingResult doctorAppointmentBindingResult, Principal user){
        // Inizializzazione variabili utili
        String message;
        String emailMessage;
        List<String> errorMessagesToShow = new ArrayList<>();
        String errorFromFromData = null;
        /*RACCOLTA DEGLI ERRORI DI VALIDAZIONE
        GLI ERRORI VENGONO MOSTRATI A SCHERMO TRAMITE LA LISTA errorMessagesToShow
        CHE RACCOGLIE IL CAMPO NON VALIDO (error.getField()) E IL MESSAGGIO DI ERRORE (error.getDefaultMessage())*/
        // Se ci sono errori di validazione //
        // 1. ERRORI RIFERITI AGLI ATTRIBUTI DELLA CLASSE UserRegistrationDto
        if(doctorAppointmentBindingResult.hasErrors()) {
            // Per ogni errore di validazione trovato da BindingResult //
            for (ObjectError error : doctorAppointmentBindingResult.getAllErrors()) {
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
            return "doctor/doctorAgenda/doctorAgendaMessage";
        }
        try{
            // Recupero le informazioni sul medico, inserisco l'oggetto di tipo Doctor nell'appuntamento e salvo
            // l'appuntamento.
            Doctor doctor = customDoctorDetailService.findDoctorByUsername(user.getName());
            appointment.setDoctor(doctor);
            // Salviamo l'appointment.
            appointmentService.saveDoctorAppointment(appointment, doctor.getId());
            message = "Appuntamento salvato con successo";
            model.addAttribute("message", message);
            // INVIO DELLA EMAIL
            emailSenderService.sendEmail(appointment);
            emailMessage = "Email inviata con successo";
            model.addAttribute("emailMessage", emailMessage);
        } catch(SendFailedException e){
            // Recupera l'eventuale errore se non riesce ad inviare l'email.
            emailMessage = e.getMessage();
            model.addAttribute("emailMessage", emailMessage);
        } catch (NullPointerException e){
            // Recuper l'eventuale errore se non viene trovato nessun medico corrispondente all'utente che
            // sta cercando di creare un appuntamento.
            message = e.getMessage();
            model.addAttribute("message", message);
        } catch(Exception e){
            message = e.getMessage();
            model.addAttribute("message", message);
        }
        // indirizzo pagina che verrà visualizzata
        return "doctor/doctorAgenda/doctorAgendaMessage";
    }

    /**
     * Metodo che permette la cancellazione di un appuntamento tramite il suo id
     * @param appointmentId id che identifica un record specifico nella tabella appointment.
     * @param user utente loggato.
     * @param model modello da utilizzare per riportare le informazioni su Thymeleaf
     * @return
     */
    @DeleteMapping("agenda/deleteAppointment/{id}")
    public String deleteDraftDiaryEntry(@PathVariable("id") int appointmentId, Principal user, Model model){
        // Inizializzazione delle variabili utili
        String message = "";
        try {
            // Controllo che il paziente esista
            Patient patient = customPatientDetailService.findPatientByUserUsername(user.getName());
            // Controllo che la combinazione paziente - appuntamento esista.
            Appointment appointment = appointmentService.checkAppointmentUser(patient.getId(), appointmentId);
            // Se l'appuntamento è stato inserito dal medico, il paziente non può cancellarlo.
            if(appointment.getDoctor() != null){
                throw new Exception("Appuntamento inserito dal medico. Non è possibile cancellarlo");
            }
            // cancello l'appuntamento.
            appointmentService.deleteAppointment(appointmentId);
        } catch(NullPointerException e){
            // Raccoglie l'eventuale errore se non esiste il paziente o se non esiste la combinazione paziente - appuntamento.
            message = e.getMessage();
            model.addAttribute("message", message);
            // indirizzo pagina che verrà visualizzata
            return "agenda/agendaMessage.html";
        } catch(Exception e){
//            message = "Il record selezionato non appartiene all'utente loggato. Per il corretto funzionamento, procedi al logout e login";
            message = e.getMessage();
            model.addAttribute("message", message);
            // indirizzo pagina che verrà visualizzata
            return "agenda/agendaMessage.html";
        }
        message = "Cancellazione avvenuta con successo";
        model.addAttribute("message", message);
        // indirizzo pagina che verrà visualizzata
        return "agenda/agendaMessage.html";
    }

    /**
     * Recupera tutti i futuri appuntamenti di un paziente in base al suo id.
     * @param patientId identificativo del paziente di cui si vogliono recuperare gli appuntamenti.
     * @param user utente loggato -- deve essere un medico
     * @param model oggetto che permette di portare al front end i valori recuperati.
     * @return la pagina dove vedere gli appuntamenti del paziente.
     */
    @GetMapping(value="doctor/doctorAgenda/appointmentPerPatient/{id}")
    public String showAllTheFuturePatientAppointmentToDoctor(@PathVariable("id") int patientId, Principal user, Model model){
        String message;
        try{
            // Verifico che l'utente loggato sia un medico
            Doctor doctor = customDoctorDetailService.findDoctorByUsername(user.getName());
            // Verifico che l'utente di cui voglio recuperare i dati sia un paziente del medico loggato
            Optional<Patient> optionalPatient = customPatientDetailService.findByIdAndDoctorId(patientId, doctor.getId());
            optionalPatient.ifPresent(patient -> {
                List<Appointment> appointments = appointmentService.getAllAppointmentsByPatientId(patientId);
                model.addAttribute("appointments", appointments);
            });
            optionalPatient.orElseThrow();
            return "doctor/doctorAgenda/patientAppointments";
        }catch(Exception e){
            message = e.getMessage();
            model.addAttribute("message", message);
            return "/myErrorpage.html";
        }
    }


}

