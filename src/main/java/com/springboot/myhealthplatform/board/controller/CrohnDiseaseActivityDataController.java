package com.springboot.myhealthplatform.board.controller;

import com.springboot.myhealthplatform.bean.Doctor;
import com.springboot.myhealthplatform.bean.Patient;
import com.springboot.myhealthplatform.board.bean.CrohnDiseaseActivityData;
import com.springboot.myhealthplatform.board.service.CrohnDiseaseActivityDataService;
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
import java.time.LocalDateTime;
import java.util.*;
/**
 * Classe Controller riceve gli user input relativi agli appuntamenti (classe CrohnDiseaseActivityData),
 * li elabora e invia la richiesta al Service opportuno; allo stesso tempo riceve le informazioni
 * richieste dal Service e le elabora e le invia all'interfaccia utente.
 */
@Controller
public class CrohnDiseaseActivityDataController {

    @Autowired
    CrohnDiseaseActivityDataService crohnDiseaseActivityDataService;
    @Autowired
    CustomPatientDetailService customPatientDetailService;
    @Autowired
    CustomDoctorDetailService customDoctorDetailService;

    /**
     * Recupero dei dati utili per il calcolo degli indici di attività della malattia per singolo paziente,
     * recuperato tramite id.
     * @param model modello da utilizzare per riportare le informazioni su Thymeleaf
     * @param user utente loggato
     * @param patientId identificativo del paziente scelto per il calcolo.
     * @return la form per l'inserimento delle variabili utili al calcolo dell'indice.
     */
    @GetMapping("doctor/activityIndex/activityIndexFormPerPatient/{id}")
    public String displayActivityIndexForm(Model model, Principal user, @PathVariable("id") int patientId){
        // Inizializzazione delle variabili utili.
        String message;
        try{
            // Recupera il medico tramite lo username dell'utente loggato.
            Doctor doctor = customDoctorDetailService.findDoctorByUsername(user.getName());
            // Recupera il paziente, verificando che la combinazione paziente - dottore loggato.
            Optional<Patient> optionalPatient = customPatientDetailService.findByIdAndDoctorId(patientId, doctor.getId());
            optionalPatient.ifPresent(patient -> {
                model.addAttribute("patient", patient);
            });
        } catch (NoSuchElementException e){
            // Recupera l'errore lanciato se non viene trovata la combinazione paziente - dottore.
            message = "Paziente o Dottore non trovato";
            model.addAttribute("message", message);
            return "doctor/doctorErrorPage.html";
        } catch (Exception e){
            message = e.getMessage();
            model.addAttribute("message", message);
            return "/myErrorPage.html";
        }
        // indirizzo pagina che verrà visualizzata
        return "doctor/activityIndex/activityIndexForm.html";
    }

    /**
     * Calcolo dell'indice di attività richiesto nella form. Dopo la validazione dell'input, in base all'indice richiesto,
     * il sistema richiama la funzione service utile al calcolo e riceve il risultato che poi verrà mostrato
     * nella pagina html.
     * @param model modello da utilizzare per riportare le informazioni su Thymeleaf
     * @param crohnDiseaseActivityData oggetto che contiene i dati recuperati dalla form.
     * @param bindingResult oggetto che contiene tutti gli errori di validazione della form.
     * @param calculationStrategy identifica quale o quali metodi sono stati scelti per calcolare l'indice
     *                            di attività della malttia.
     * @param user utente loggato
     * @return la pagina html dove verranno mostrati i risultati del calcolo degli indici.
     */
    @PostMapping("doctor/activityIndex/activityIndexCalculator")
    public String crohnDiseaseActivityIndexCalculation(Model model, @Valid @ModelAttribute("crohnDiseaseActivityData") CrohnDiseaseActivityData crohnDiseaseActivityData, BindingResult bindingResult, @RequestParam("calculationStrategy") int calculationStrategy, Principal user){
        // inizializzazione delle variabili //
        String message;
        int HBIValue = -1;
        int outputIndexValue = -1;
        List<String> errorMessagesToShow = new ArrayList<>();
        String errorFromFromData = null;
        /*RACCOLTA DEGLI ERRORI DI VALIDAZIONE
        GLI ERRORI VENGONO MOSTRATI A SCHERMO TRAMITE LA LISTA errorMessagesToShow
        CHE RACCOGLIE IL CAMPO NON VALIDO (error.getField()) E IL MESSAGGIO DI ERRORE (error.getDefaultMessage())*/
        // Se ci sono errori di validazione //
        if(bindingResult.hasErrors()){
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
            return "doctor/activityIndex/AIForm";
        }
        // SE non sono stati riscontrati errori di validazione si procede al calcolo dell'activity index richiesto.
        try{
            crohnDiseaseActivityData.setFormDate(LocalDateTime.now());
            if(calculationStrategy == 0) {
                outputIndexValue = crohnDiseaseActivityDataService.calculateCDAI(crohnDiseaseActivityData);
            } else if(calculationStrategy == 1){
                HBIValue = crohnDiseaseActivityDataService.calculateHBI(crohnDiseaseActivityData);
            } else if (calculationStrategy == 2){
                outputIndexValue = crohnDiseaseActivityDataService.calculateCDAI(crohnDiseaseActivityData);
                HBIValue = crohnDiseaseActivityDataService.calculateHBI(crohnDiseaseActivityData);
            }
            model.addAttribute("CDAIValue",outputIndexValue);
            model.addAttribute("HBIValue", HBIValue);
        }catch (Exception e){
            message = "Qualcosa è andato storto";
            model.addAttribute("message", message);
            return "doctor/doctorErrorPage.html";
        }
        // indirizzo pagina che verrà visualizzata
        return "doctor/activityIndex/indexValueResult.html";
    }

}
