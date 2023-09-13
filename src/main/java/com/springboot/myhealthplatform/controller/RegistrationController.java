package com.springboot.myhealthplatform.controller;

import com.springboot.myhealthplatform.bean.UserRegistrationDto;
import com.springboot.myhealthplatform.login.CustomUserDetailsService;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
/**
 * Controller relativo all'oggetto UserRegistrationDto.
 */
@Controller
@RequestMapping("/registration")
public class RegistrationController {

    private final CustomUserDetailsService customerUserDetailService;

    public RegistrationController(CustomUserDetailsService customerUserDetailService) {
        super();
        this.customerUserDetailService = customerUserDetailService;
    }

    @ModelAttribute("user")
    public UserRegistrationDto userRegistrationDto() {
        return new UserRegistrationDto();
    }

    @GetMapping
    public String showRegistrationForm() {
        return "registration";
    }

    /**
     * Metodo che riceve i dati derivanti dalla form di registrazione per utenti Admin o Nurse, verifica
     * i valori in ingresso (la loro validità e eventuali controlli legati all'univocità del dato) e invia i dati
     * al CustomUserDetailsService che li salverà poi nel database.
     * Nel caso ci fossero degli errori di validazione, il sistema lancerà un ValidationException e bloccherà
     * la registrazione del nuovo utente.
     * @param model oggetto che permette di portare dati nel front end che poi li potrà manipolare.
     * @param registrationDto oggetto che raccoglie i dati provenienti dalla form di registrazione.
     * @param bindingResult oggetto che raccoglie eventuali errori di validazione.
     * @param role oggetto Role che contiene il ruolo da assegnare al nuovo utente.
     * @return l'indirizzo URL di avvenuto successo della registrazione oppure, se è stato lanciato un errore,
     * l'URL della pagina della form, dove verranno mostrati tutti gli errori trovati.
     */
    @PostMapping
    public String registerUserAccount(Model model, @Valid @ModelAttribute("user")
                                      UserRegistrationDto registrationDto, BindingResult bindingResult, @RequestParam("radioRole") String role) {
        // Inizializzazione variabili per mostrare messaggi a schermo
        String message;
        List<String> errorMessagesToShow = new ArrayList<>();
        String errorFromFromData = null;
        /*RACCOLTA DEGLI ERRORI DI VALIDAZIONE
        GLI ERRORI VENGONO MOSTRATI A SCHERMO TRAMITE LA LISTA errorMessagesToShow
        CHE RACCOGLIE IL CAMPO NON VALIDO (error.getField()) E IL MESSAGGIO DI ERRORE (error.getDefaultMessage())*/
/*        Se ci sono errori di validazione
        1. ERRORI RIFERITI AGLI ATTRIBUTI DELLA CLASSE UserRegistrationDto*/
        if (bindingResult.hasErrors()) {
            // Per ogni errore di validazione trovato da BindingResult //
            for (ObjectError error : bindingResult.getAllErrors()) {
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
            return "/admin/registration.html";
        }
        try{
            if(role.isEmpty() || role.isBlank()){
                throw new Exception("Ruolo non selezionato");
            }
            registrationDto.setRole(role);
            customerUserDetailService.findByUsernameForValidation(registrationDto.getUsername());
            customerUserDetailService.findEmailForValidation(registrationDto.getEmail());
            customerUserDetailService.findByNameAndSurnameForValidation(registrationDto.getName(), registrationDto.getSurname());
            customerUserDetailService.save(registrationDto);
        } catch (ValidationException e){
            message = e.getMessage();
            errorMessagesToShow.add(message);
            model.addAttribute("errorMessages", errorMessagesToShow);
            return "/admin/registration.html";
        } catch(Exception e){
            message = e.getMessage();
            errorMessagesToShow.add(message);
            model.addAttribute("errorMessages", errorMessagesToShow);
            return "/admin/registration.html";
        }
//        return "redirect:/registration?success";
            return "/admin/successRegistration.html";
    }

}