package com.springboot.myhealthplatform.board.controller;

import com.springboot.myhealthplatform.bean.Patient;
import com.springboot.myhealthplatform.board.bean.BloodTestReport;
import com.springboot.myhealthplatform.board.service.BloodTestReportService;
import com.springboot.myhealthplatform.service.CustomPatientDetailService;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
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
 * Classe Controller riceve gli user input relativi agli esami del sangue (classe BloodTestReport),
 * li elabora e invia la richiesta al Service opportuno; allo stesso tempo riceve le informazioni
 * richieste dal Service e le elabora e le invia all'interfaccia utente.
 */
@Controller
public class BloodTestReportController {

    @Autowired
    private BloodTestReportService bloodTestReportService;
    @Autowired
    private CustomPatientDetailService customPatientDetailService;

    /**
     * Recupera tutti i dati riferiti alla tabella "blood_test_report" presenti su DB e
     * recupera tutti gli eventuali oggetti di classe BloodTestReport che corrispondono alle bozze non
     * ancora caricate a sistema da parte del paziente
     * Il tutto viene inviato al Front-end, alla pagina html 'bloodExams' tramite la classe Model.
     * @param model oggetto della classe Model che permette il passaggio dei dati al front-end;
     * @param user utente loggato a sistema;
     * @return la pagina html dove visualizzare i dati.
     */
    @GetMapping("reports/bloodExams")
    public String getListBloodTestReports(Model model, Principal user){
        // Inizializzazione variabili utili;
        String userUsername = user.getName();
        Patient patient;
        try {
            // Cerca il paziente nel database
            patient = customPatientDetailService.findPatientByUserUsername(userUsername);
            // Recupera tutti gli esami del sangue del paziente.
            List<BloodTestReport> bloodTestReports = bloodTestReportService.getAllBloodTestReports(patient.getId());
            model.addAttribute("bloodTestReports", bloodTestReports);
        } catch(ValidationException e){
            // Nel caso il paziente risultasse inesistente
            String message = e.getMessage();
            model.addAttribute("message", message);
            return "/reports/bloodExams.html";
        }
        // indirizzo pagina che verrà visualizzata
        return "/reports/bloodExams.html";
    }

    /**
     * Servlet di tipo POST che ricevein ingresso i dati della form compilata dall'utente, riferita agli esami del
     * sangue (bloodTestReport), collega il referto all'utente loggato, che ha compilato la form, e invia
     * l'informazione al Service di riferimento per poter essere poi salvato sul DB. Restituisce eventuali errori
     * relativi ad errori di caricamento sul DB o errori riferiti allo stato dell'oggetto BloodTestReport. Se il
     * caricamento su DB va a buon fine, viene mostrato un messaggio che informa l'utente del successo dell'operazione.
     * @param model oggetto di classe Model che permette il passaggio di informazioni al front-end. In questo caso si
     *              parla di messaggi di errore, provenienti dal DB o dallo stato dell'oggetto. Se il
     *              caricamento su DB va a buon fine, viene mostrato un messaggio che informa l'utente del successo
     *              dell'operazione.
     * @param bloodTestReport oggetto che contiene al suo interno tutte le informazioni provenienti dalla form
     * @param user utente loggato che ha compilato la form
     * @return la pagina html 'bloodExamForm'
     */
    @RequestMapping(value="reports/bloodExamUpload", method=RequestMethod.POST, params="action=Upload")
    public String bloodExamUpload(Model model, @Valid @ModelAttribute("bloodTestReport") BloodTestReport bloodTestReport, BindingResult bloodTestBindingResult, Principal user){
        // Inizializzazione delle variabili utili a portare messaggi al front end
        String message = "";
        List<String> errorMessagesToShow = new ArrayList<>();
        String errorFromFromData = null;
        /*RACCOLTA DEGLI ERRORI DI VALIDAZIONE
        GLI ERRORI VENGONO MOSTRATI A SCHERMO TRAMITE LA LISTA errorMessagesToShow
        CHE RACCOGLIE IL CAMPO NON VALIDO (error.getField()) E IL MESSAGGIO DI ERRORE (error.getDefaultMessage())*/
        // Se ci sono errori di validazione //
        // 1. ERRORI RIFERITI AGLI ATTRIBUTI DELLA CLASSE UserRegistrationDto
        if(bloodTestBindingResult.hasErrors()) {
            // Per ogni errore di validazione trovato da BindingResult //
            for (ObjectError error : bloodTestBindingResult.getAllErrors()) {
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
            return "reports/bloodExamForm.html";
        }
        try {
            // Validazione della data, che deve essere antecedente ad oggi.
            if(bloodTestReport.getExaminationDate().after(new Date())){
                throw new ValidationException("The date is not valid");
            }
            // Verifichiamo che il paziente loggato sia correttamente esistente.
            Patient patient = customPatientDetailService.findPatientByUserUsername(user.getName());
            // Se il paziente esiste, allora inseriscilo nell'oggetto Patient legato all'oggetto
            // bloodTestReport.
            bloodTestReport.setPatient(patient);
            // Verifichiamo che non esistano delle analisi registrate in questa data
            bloodTestReportService.findByDateForValidation(bloodTestReport.getExaminationDate(), patient.getId());
            // Verifichiamo che per il paziente non esistano altre analisi registrate con questa descrizione
            bloodTestReportService.findByDescriptionForValidation(bloodTestReport.getDescription(), patient.getId());
            // Salva il report nel DB
            bloodTestReportService.save(bloodTestReport);
            message = "Uploaded the file successfully: " + bloodTestReport.getDescription();
            //return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
            model.addAttribute("message", message);
        } catch(ValidationException e){
            // Recupera eventuali errori di validazione lanciati durante i controlli del blocco try
            errorFromFromData =e.getMessage();
            errorMessagesToShow.add(errorFromFromData);
            model.addAttribute("errorMessages", errorMessagesToShow);
            return "reports/bloodExamForm.html";
        } catch (Exception e) {
            message = "Could not upload the file: " + bloodTestReport.getDescription() + "!";
            model.addAttribute("message", message);
            return "reports/bloodExamForm.html";
        }
        // indirizzo pagina che verrà visualizzata
        return "reports/bloodExamForm.html";
    }
}
