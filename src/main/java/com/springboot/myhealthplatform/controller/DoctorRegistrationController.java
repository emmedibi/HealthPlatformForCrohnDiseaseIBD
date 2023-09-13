package com.springboot.myhealthplatform.controller;

import com.springboot.myhealthplatform.bean.DoctorRegistrationDto;
import com.springboot.myhealthplatform.bean.UserRegistrationDto;
import com.springboot.myhealthplatform.login.CustomUserDetailsService;
import com.springboot.myhealthplatform.service.CustomDoctorDetailService;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
/**
 * Controller relativo all'oggetto DoctorRegistrationDto.
 */
@Controller
@RequestMapping("/doctorRegistration")
public class DoctorRegistrationController {

    private final CustomDoctorDetailService customDoctorDetailService;
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * Costruttore
     * @param customDoctorDetailService service della classe Doctor
     * @param customUserDetailsService service della classe User
     */
    public DoctorRegistrationController(CustomDoctorDetailService customDoctorDetailService, CustomUserDetailsService customUserDetailsService){
        super();
        this.customDoctorDetailService = customDoctorDetailService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @ModelAttribute("user")
    public UserRegistrationDto userRegistrationDto() {
        return new UserRegistrationDto();
    }

    @ModelAttribute("doctor")
    public DoctorRegistrationDto doctorRegistrationDto() {
        return new DoctorRegistrationDto();
    }

    @GetMapping
    public String showDoctorRegistrationForm() {
        return "doctorRegistrationPage";
    }

    /**
     * Metodo che riceve i dati derivanti dalla form di registrazione per utenti Doctor, verifica
     * i valori in ingresso (la loro validità e eventuali controlli legati all'univocità del dato) e invia i dati
     * al CustomDoctorDetailsService che li salverà poi nel database.
     * Nel caso ci fossero degli errori di validazione, il sistema lancerà un ValidationException e bloccherà
     * la registrazione del nuovo utente.
     * @param model oggetto che permette di portare dati nel front end che poi li potrà manipolare.
     * @param userRegistrationDto oggetto che raccoglie i dati provenienti dalla form di registrazione che
     *                             andranno a creare il record nella tabella user.
     * @param userBindingResult oggetto che raccoglie eventuali errori di validazione relativi all'oggetto
     *                          userRegistrationDto.
     * @param doctorRegistrationDto oggetto che raccoglie i dati provenienti dalla form di registrazione che
     *                               andranno a creare il record nella tabella doctor.
     * @param doctorBindingResult oggetto che raccoglie eventuali errori di validazione relativi all'oggetto
     *                             doctorRegistrationDto.
     * @return L'URL della form di registrazione, se sono stati trovati degli errori durante la registrazione, altrimenti
     *         recupera la pagina di registrazione avvenuta con successo.
     */
    @PostMapping
    public String registerDoctorAndUserAccount(Model model, @Valid @ModelAttribute("user") UserRegistrationDto userRegistrationDto, BindingResult userBindingResult, @Valid @ModelAttribute("doctor") DoctorRegistrationDto doctorRegistrationDto, BindingResult doctorBindingResult){
        // Inizializzazione variabili per mostrare messaggi a schermo
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
            // la lista viene inserita nel model che mostrerà poi i dati all'utente.
            model.addAttribute("errorMessages", errorMessagesToShow);
            // Si viene rindirizzati nuovamente alla pagina della form.
            return "/admin/doctorRegistrationPage.html";
            //    2. ERRORI RIFERITI AGLI ATTRIBUTI DELLA CLASSE PatientRegistrationDto
        } else if(doctorBindingResult.hasErrors()){
            // Per ogni errore di validazione trovato da BindingResult //
            for (ObjectError error : doctorBindingResult.getAllErrors()) {
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
            return "/admin/doctorRegistrationPage.html";
        }
        // VALIDAZIONE DATI UNIVOCI CHE NON DEVONO ESSERE GIA' PRESENTI NEL DB
        try{
            // VALIDAZIONE PER VALORI UNIVOCI MA GIà PRESENTI SUL DB
            // lo username non deve già essere stato usato
            customUserDetailsService.findByUsernameForValidation(userRegistrationDto.getUsername());
            // la mail non deve essere già stata utilizzata
            customUserDetailsService.findEmailForValidation(userRegistrationDto.getEmail());
            // il codice fiscale non deve essere già registrato
            customDoctorDetailService.findCFForValidation(doctorRegistrationDto.getCF());
            // il badge number non deve essere già registrato per altri utenti
            customDoctorDetailService.findBadgeNumberForValidation(doctorRegistrationDto.getBadgeNumber());
            // Se ok, procedo alla registrazione del paziente nel DB
            // inserisco il ruolo e l'id del medico
            userRegistrationDto.setRole("ROLE_DOCTOR");
            doctorRegistrationDto.setUser(userRegistrationDto);
            customDoctorDetailService.save(userRegistrationDto, doctorRegistrationDto);
            // Se non ci sono errori nella procedura, inserisco il messaggio di registrazione avvenuta con successo
            message = "Registrazione avvenuta con successo!";
            model.addAttribute("message", message);
        } catch(ValidationException e){
            message = e.getMessage();
            errorMessagesToShow.add(message);
            model.addAttribute("errorMessages", errorMessagesToShow);
            return "/admin/doctorRegistrationPage.html";
        } catch (Exception e){
            message = "Si è verificato un errore nel tentativo di registrare l'utente. Riprovare.";
            errorMessagesToShow.add(message);
            model.addAttribute("message", errorMessagesToShow);
            return "/admin/doctorRegistrationPage.html";
        }
        return "/admin/successRegistration.html";
    }



}
