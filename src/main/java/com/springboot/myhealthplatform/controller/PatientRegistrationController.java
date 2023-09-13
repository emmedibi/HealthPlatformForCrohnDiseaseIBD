package com.springboot.myhealthplatform.controller;

import com.springboot.myhealthplatform.bean.Doctor;
import com.springboot.myhealthplatform.bean.PatientRegistrationDto;
import com.springboot.myhealthplatform.bean.UserRegistrationDto;
import com.springboot.myhealthplatform.login.CustomUserDetailsService;
import com.springboot.myhealthplatform.service.CustomDoctorDetailService;
import com.springboot.myhealthplatform.service.CustomPatientDetailService;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 * Controller relativo all'oggetto PatientRegistrationDto.
 */
@Controller
@RequestMapping("/patientRegistration")
public class PatientRegistrationController {

    private final CustomPatientDetailService customPatientDetailService;
    private final CustomDoctorDetailService customDoctorDetailService;
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * Costruttore della classe
     * @param customPatientDetailService service della classe Patient
     * @param customDoctorDetailService service della classe Doctor
     * @param customUserDetailsService service della classe User
     */
    public PatientRegistrationController(CustomPatientDetailService customPatientDetailService, CustomDoctorDetailService customDoctorDetailService, CustomUserDetailsService customUserDetailsService){
        super();
        this.customPatientDetailService = customPatientDetailService;
        this.customDoctorDetailService = customDoctorDetailService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @ModelAttribute("user")
    public UserRegistrationDto userRegistrationDto(){
        return new UserRegistrationDto();
    }

    @ModelAttribute("patient")
    public PatientRegistrationDto patientRegistrationDto(){
        return new PatientRegistrationDto();
    }

    /** -- NOT USED --
     *
     * @return
     */
    @GetMapping
    public String showPatientRegistrationForm() { return "patientRegistration";}

    /**
     * Metodo che riceve i dati derivanti dalla form di registrazione per utenti Patient, verifica
     * i valori in ingresso (la loro validità e eventuali controlli legati all'univocità del dato) e invia i dati
     * al CustomPatientDetailsService che li salverà poi nel database.
     * Nel caso ci fossero degli errori di validazione, il sistema lancerà un ValidationException e bloccherà
     * la registrazione del nuovo utente.
     * @param model oggetto che permette di portare dati nel front end che poi li potrà manipolare.
     * @param userRegistrationDto oggetto che raccoglie i dati provenienti dalla form di registrazione che
     *                            andranno a creare il record nella tabella user.
     * @param userBindingResult oggetto che raccoglie eventuali errori di validazione relativi all'oggetto
     *                          userRegistrationDto.
     * @param patientRegistrationDto oggetto che raccoglie i dati provenienti dalla form di registrazione che
     *                               andranno a creare il record nella tabella patient.
     * @param patientBindingResult oggetto che raccoglie eventuali errori di validazione relativi all'oggetto
     *                             patientRegistrationDto.
     * @param doctor_id id del medico che prenderà in carico il paziente nella struttura. L'id è utile per recuperare
     *                  l'oggetto doctor corripondente e inserirlo nell'oggetto patient prima di salvare quest'ultimo
     *                  nel database.
     * @return L'URL della form di registrazione, se sono stati trovati degli errori durante la registrazione, altrimenti
     * recupera la pagina di registrazione avvenuta con successo.
     * @throws Exception viene lanciata per la verifica che viene fatta sulla validità della data di nascita del paziente.
     */
    @PostMapping
    public String registerPatientAndUserAccount(Model model, @Valid @ModelAttribute("user") UserRegistrationDto userRegistrationDto, BindingResult userBindingResult, @Valid @ModelAttribute("patient") PatientRegistrationDto patientRegistrationDto, BindingResult patientBindingResult, @RequestParam("doctor") int doctor_id) throws Exception{
        String message;
        List<String> errorMessagesToShow = new ArrayList<>();
        String errorFromFromData = null;
        /*RACCOLTA DEGLI ERRORI DI VALIDAZIONE
        GLI ERRORI VENGONO MOSTRATI A SCHERMO TRAMITE LA LISTA errorMessagesToShow
        CHE RACCOGLIE IL CAMPO NON VALIDO (error.getField()) E IL MESSAGGIO DI ERRORE (error.getDefaultMessage())*/
        // Se ci sono errori di validazione //
        // 1. ERRORI RIFERITI AGLI ATTRIBUTI DELLA CLASSE UserRegistrationDto
        if(userBindingResult.hasErrors()){
            // Per ogni errore di validazione trovato da BindingResult //
            for (ObjectError error : userBindingResult.getAllErrors()) {
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
            // Recupero nuovamente i dati dei medici
            List<Doctor> doctors = customDoctorDetailService.getAll();
            model.addAttribute("doctors", doctors);
            // la lista viene inserita nel model che mostrerà poi i dati all'utente.
            model.addAttribute("errorMessages", errorMessagesToShow);
            // Si viene rindirizzati nuovamente alla pagina della form.
            return "/admin/patientRegistration.html";

        //    2. ERRORI RIFERITI AGLI ATTRIBUTI DELLA CLASSE PatientRegistrationDto
        } else if(patientBindingResult.hasErrors()){
            // Per ogni errore di validazione trovato da BindingResult //
            for (ObjectError error : patientBindingResult.getAllErrors()) {
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
            // Recupero nuovamente i dati dei medici
            List<Doctor> doctors = customDoctorDetailService.getAll();
            model.addAttribute("doctors", doctors);
            // la lista viene inserita nel model che mostrerà poi i dati all'utente.
            model.addAttribute("errorMessages", errorMessagesToShow);
            // Si viene rindirizzati nuovamente alla pagina della form.
            return "/admin/patientRegistration.html";
        }
        // Controlli di validità: congruenza dati
        // 1. Validità data di nascita: l'utente non può avere meno di 1 anno e più di 100
        LocalDate dobNotBeforeThisDate = LocalDate.of(LocalDate.now().getYear()-100, LocalDate.now().getMonthValue(), LocalDate.now().getDayOfMonth());
        LocalDate dobNotAfterThisDate = LocalDate.of(LocalDate.now().getYear()-1, LocalDate.now().getMonthValue(), LocalDate.now().getDayOfMonth());
/*        LocalDateTime dobNotBeforeThisDate = LocalDateTime.of(LocalDateTime.now().getYear()-100, LocalDateTime.now().getMonth(), LocalDateTime.now().getDayOfMonth(), 00, 00);
        LocalDateTime dobNotAfterThisDate = LocalDateTime.of(LocalDateTime.now().getYear()-1, LocalDateTime.now().getMonth(), LocalDateTime.now().getDayOfMonth(), 00, 00);*/
        if(patientRegistrationDto.getDOB().isAfter(dobNotAfterThisDate) || patientRegistrationDto.getDOB().isBefore(dobNotBeforeThisDate)){
            message = "Date of birth not valid. The user has to be more than 1 year and less tha 100 years.";
            errorMessagesToShow.add(message);
            List<Doctor> doctors = customDoctorDetailService.getAll();
            model.addAttribute("doctors", doctors);
            model.addAttribute("errorMessages", errorMessagesToShow);
            return "/admin/patientRegistration.html";
        }
        try{
            // VALIDAZIONE PER VALORI UNIVOCI MA GIà PRESENTI SUL DB
            // lo username non deve già essere stato usato
            customUserDetailsService.findByUsernameForValidation(userRegistrationDto.getUsername());
            // la mail non deve essere già stata utilizzata
            customUserDetailsService.findEmailForValidation(userRegistrationDto.getEmail());
            // il codice fiscale non deve essere già registrato
            customPatientDetailService.findCFForValidation(patientRegistrationDto.getCF());
            // Se ok, procedo alla registrazione del paziente nel DB
            // inserisco il ruolo e l'id del medico
            userRegistrationDto.setRole("ROLE_PATIENT");
            patientRegistrationDto.setDoctorId(doctor_id);
            customPatientDetailService.save(userRegistrationDto, patientRegistrationDto);
            // Se non ci sono errori nella procedura, inserisco il messaggio di registrazione avvenuta con successo
            message = "Registrazione avvenuta con successo!";
            model.addAttribute("message", message);
        } catch(ValidationException e){
            message = e.getMessage();
            errorMessagesToShow.add(message);
            model.addAttribute("errorMessages", errorMessagesToShow);
            List<Doctor> doctors = customDoctorDetailService.getAll();
            model.addAttribute("doctors", doctors);
            return "/admin/patientRegistration.html";
        } catch (Exception e){
            message = "Si è verificato un errore nel tentativo di registrare l'utente. Riprovare.";
            errorMessagesToShow.add(message);
            model.addAttribute("message", errorMessagesToShow);
            return "/admin/patientRegistration.html";
        }
        return "/admin/successRegistration.html";
    }

}
