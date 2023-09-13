package com.springboot.myhealthplatform.board.controller;

import com.springboot.myhealthplatform.bean.Patient;
import com.springboot.myhealthplatform.board.bean.DiaryEntry;
import com.springboot.myhealthplatform.board.designPattern.State;
import com.springboot.myhealthplatform.board.service.DiaryEntryService;
import com.springboot.myhealthplatform.service.CustomDoctorDetailService;
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
import java.util.*;
/**
 * Classe Controller riceve gli user input relativi agli appuntamenti (classe DiaryEntry),
 * li elabora e invia la richiesta al Service opportuno; allo stesso tempo riceve le informazioni
 * richieste dal Service e le elabora e le invia all'interfaccia utente.
 */
@Controller
public class DiaryEntryController {

    @Autowired
    private DiaryEntryService diaryEntryService;
    @Autowired
    private CustomPatientDetailService customPatientDetailService;
    @Autowired
    private CustomDoctorDetailService customDoctorDetailService;

    public DiaryEntryController(DiaryEntryService diaryEntryService, CustomPatientDetailService customPatientDetailService, CustomDoctorDetailService customDoctorDetailService) {
        this.diaryEntryService = diaryEntryService;
        this.customPatientDetailService = customPatientDetailService;
        this.customDoctorDetailService = customDoctorDetailService;
    }

    /**
     * Recupera le pagine di diario scritte dall'utente loggato. Se l'utente loggato non è un paziente, il sistema restituisce
     * un'eccezione.
     * @param model oggetto che permette il passaggio dei dati al front end.
     * @param user utente loggato
     * @return restituisce la pagina html dove verrà mostrata la lista di oggetti di classe DiaryEntry recuperati.
     */
    @GetMapping("diary/diaryEntries")
    public String getListDiaryEntries(Model model, Principal user){
    try {
        // L'utente deve essere un paziente, quindi ricerco il paziente nel db
        Patient patient = customPatientDetailService.findPatientByUserUsername(user.getName());
        // Estraggo tutte le pagine di diario Pubblicate del paziente loggato.
        List<DiaryEntry> diaryEntries = diaryEntryService.getAllPublishedDiaryEntries(patient.getId());
        model.addAttribute("diaryEntries", diaryEntries);
        // Estraggo tutte le pagine di diario in Bozza del paziente loggato.
        List<DiaryEntry> diaryEntryDrafts = diaryEntryService.getAllDraftDiaryEntries(patient.getId());
        model.addAttribute("diaryEntryDrafts", diaryEntryDrafts);
    } catch (NullPointerException e){
        // Errore catturato se non viene trovato nessun paziente corrispondente allo username.
        String message = e.getMessage();
        model.addAttribute("message", message);
        return "/myErrorPage.html";
    }
        // indirizzo pagina che verrà visualizzata
     return "diary/diaryEntries.html";
    }

    @GetMapping("/patient/showDiaryEntry/{id}")
    public String showADiaryEntry(Model model, @PathVariable("id") int diaryEntryId, Principal user){
        try {
            // Verifica che l'utente selezionato sia un paziente
            Patient patient = customPatientDetailService.findPatientByUserUsername(user.getName());
            // Estrae la pagina di diario che corrisponde alla combinazione paziente - pagina di diario.
            DiaryEntry diaryEntry = diaryEntryService.checkDiaryEntryUser(patient.getId(), diaryEntryId);
            model.addAttribute("diaryEntry", diaryEntry);
        } catch (NullPointerException e){
            // Errore catturato se non viene trovato nessun paziente corrispondente allo username.
            String errorMessage = e.getMessage();
            model.addAttribute("errorMessage", errorMessage);
        }
        // indirizzo pagina che verrà visualizzata
        return "patient/showADiaryEntry.html";
    }

    /**
     * Ottiene tutte le note/pagine di diario per uno specifico paziente.
     * @param model oggetto che permette il passaggio dei dati al front end.
     * @param patientId paziente di cui si vogliono ottenere tutte le note pubblicate
     * @param user utente loggato.
     * @return la pagina "patientDiary" visibile agli infermieri. Se, invece, l'utente non fosse di ruolo ROLE_PATIENT
     * il sistema rimanda alla pagina "/myErrorPage.html" dove verrà mostrato un messaggio di errore.
     */
    @GetMapping("nurse/patientDiary/patientDiaryEntries/{id}")
    public String getPatientListDiaryEntries(Model model, @PathVariable("id") int patientId, Principal user){
        try{
            // Estraggo tutte le pagine di diario pubblicate per uno specifico paziente.
            List<DiaryEntry> diaryEntries = diaryEntryService.getAllPublishedDiaryEntries(patientId);
            model.addAttribute("diaryEntries", diaryEntries);
            model.addAttribute("patientId", patientId);
        } catch (Exception e){
            String message = e.getMessage();
            model.addAttribute("message", message);
            return "/myErrorPage.html";
        }
        // indirizzo pagina che verrà visualizzata
        return "nurse/patientDiary/patientDiaryEntriesList.html";
    }

    /**
     * Recupera le pagine di diario di un determinato paziente. Verifica anche che il paziente esista e sia associato
     * al medico loggato.
     * @param model oggetto che permette il passaggio dei dati al front end.
     * @param patientId identificativo del paziente
     * @param user utente loggato
     * @return la pagina delle note del paziente. Se la verifica dell'identità del paziente associato al medico dà esito
     * negativo, viene mostrata la pagina "/myErrorPage.html" con un messaggio personalizzato.
     */
    @GetMapping("doctor/patientDiary/diaryEntriesPerPatient/{id}")
    public String getPatientDiaryEntries(Model model, @PathVariable("id") int patientId, Principal user){
        try {
            // Verifico che l'id del paziente sia tra gli id dei pazienti legati al medico loggato.
            Optional<Patient> optionalPatient = customPatientDetailService.findByIdAndDoctorId(patientId, customDoctorDetailService.findDoctorByUsername(user.getName()).getId());
//            Patient patient = optionalPatient.get();
            // Recupero tutte le pagine di diario pubblicate per il paziente selezionato.
            List<DiaryEntry> diaryEntries = diaryEntryService.getAllPublishedDiaryEntries(patientId);
            model.addAttribute("diaryEntries", diaryEntries);
            model.addAttribute("patientId", patientId);
        } catch (NoSuchElementException e){
            String message = "Paziente non trovato per il medico loggato.";
            model.addAttribute("message", message);
            return "/myErrorPage.html";
        }
        catch(NullPointerException e){
            String message = e.getMessage();
            model.addAttribute("message", message);
            return "/myErrorPage.html";
        } catch (Exception e){
            String message = e.getMessage();
            model.addAttribute("message", message);
            return "/myErrorPage.html";
        }
        // indirizzo pagina che verrà visualizzata
        return "doctor/patientDiary/patientDiaryEntriesBoard.html";
    }

    /**
     * La pagina di diario viene pubblicata, in modo che sia visibile anche al medico e all'infermiera.
     * @param model oggetto che permette il passaggio dei dati al front end.
     * @param id identificativo della pagina di diario.
     * @param user utente loggato (deve avere ROLE_PATIENT)
     * @return la pagina dove verranno mostrate le informazioni relative alla nota del paziente. Se la verifica non ha dato
     * buon esito (la pagina non esiste o il paziente non è associato alla pagina che si sta cercando di aprire) si sarà
     * reindirizzati alla pagina "/myErrorPage.html" dove verrà mostrato un errore personalizzato.
     */
    @PutMapping("diary/publishDiaryEntry/{id}")
    public String saveDiaryEntryAsPublished(Model model, @PathVariable int id, Principal user){
        String message = "";
        try {
            // Verifico che l'utente sia un paziente
            Patient patient = customPatientDetailService.findPatientByUserUsername(user.getName());
            // Verifico che la pagina di diario che si vuole vedere sia del paziente loggato.
            DiaryEntry diaryEntry = diaryEntryService.checkDiaryEntryUser(patient.getId(), id);
            State s = diaryEntry.getState();
            s.uploadDocument(diaryEntry);
            DiaryEntry publishedDiaryEntry = diaryEntryService.findDiaryEntryById(id);
            message = "The file related to " + publishedDiaryEntry.getDate() + "has been published";
            model.addAttribute("message", message);
        } catch(IllegalStateException e){
            message = e.getMessage();
            model.addAttribute("message", message);
            return "/diary/publishedInfoMessage.html";
        } catch(NullPointerException e ) {
            message = e.getMessage();
            model.addAttribute("message", message);
            return "/myErrorPage.html";
        } catch (Exception e){
            message = "Id non trovato per il paziente loggato";
            model.addAttribute("message", message);
            return "/diary/publishedInfoMessage.html";
        }
        // indirizzo pagina che verrà visualizzata
        return "/diary/publishedInfoMessage.html";
    }

    /**
     * Permette il salvataggio di una nuova nota del diario del paziente come Bozza.
     * @param model oggetto che permette il passaggio dei dati al front end.
     * @param diaryEntry oggetto che contiene le informazioni derivanti dalla form compilata dal paziente.
     * @param diaryBindingResult oggetti che contiene gli eventuali errori di validazione della form.
     * @param user utente loggato (deve avere ROLE_PATIENT).
     * @return se la validazione e il salvataggio su DB hanno successo, il sistema riporta alla pagina della form
     * del diario, con un messaggio di avvenuto corretto salvataggio dei dati. Altrimenti viene mostrato un errore con i
     * problemi riscontrati in fase di validazione o in fase di verifica del controller/service.
     */
    @PostMapping(value="diary/saveDiaryEntryAsDraft")
    public String saveDiaryEntryAsDraft(Model model, @Valid @ModelAttribute("diaryEntry") DiaryEntry diaryEntry, BindingResult diaryBindingResult, Principal user){
        String message = "";
        List<String> errorMessagesToShow = new ArrayList<>();
        String errorFromFromData = null;
        /*RACCOLTA DEGLI ERRORI DI VALIDAZIONE
        GLI ERRORI VENGONO MOSTRATI A SCHERMO TRAMITE LA LISTA errorMessagesToShow
        CHE RACCOGLIE IL CAMPO NON VALIDO (error.getField()) E IL MESSAGGIO DI ERRORE (error.getDefaultMessage())*/
        // Se ci sono errori di validazione //
        // 1. ERRORI RIFERITI AGLI ATTRIBUTI DELLA CLASSE UserRegistrationDto
        if(diaryBindingResult.hasErrors()) {
            // Per ogni errore di validazione trovato da BindingResult //
            for (ObjectError error : diaryBindingResult.getAllErrors()) {
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
            return "diary/diaryEntryForm.html";
        }
        try {
            Patient patient = customPatientDetailService.findPatientByUserUsername(user.getName());
            // La data deve riferirsi ad un giorno passato
            if(diaryEntry.getDate().after(new Date())){
                throw new ValidationException("The date is in the future");
            } else if(diaryEntryService.findByDateAndPatientId(diaryEntry.getDate(), patient.getId()) != null){
                throw new ValidationException("The date is already present");
            }
            diaryEntry.setPatient(patient);
            diaryEntry.setEntryState("Draft");
            diaryEntryService.save(diaryEntry);
            message = "The file related to " + diaryEntry.getDate() + "has been saved as a Draft";
            model.addAttribute("message", message);
        } catch(NullPointerException e){
            message = e.getMessage();
            model.addAttribute("message", message);
        } catch(ValidationException e){
            errorFromFromData = e.getMessage();
            errorMessagesToShow.add(errorFromFromData);
            model.addAttribute("errorMessages", errorMessagesToShow);
        }
        catch (Exception e) {
            message = "Could not save the file related to date " + diaryEntry.getDate() + "!";
            model.addAttribute("message", message);
        }
        // indirizzo pagina che verrà visualizzata
        return "diary/diaryEntryForm.html";
    }

    /**
     * La pagina di diario viene recuperata tramite il suo id e i dati della pagina vengono
     * mostrati nel front end per essere modificati.
     * @param id
     * @param model
     * @param user
     * @return
     */
    @GetMapping("diary/checkDiaryEntry/{id}")
    public String getDiaryEntryDraft(@PathVariable int id, Model model, Principal user){
        // Inizializzazione delle variabili utili
        String message;
        String username;
        username = user.getName();
        Patient patient;
        try {
            // Verifico che l'utente loggato sia un paziente.
            patient = customPatientDetailService.findPatientByUserUsername(username);
            // Estraggo la pagina di diario in base all'id
            DiaryEntry diaryEntry = diaryEntryService.getDiaryEntryDraftById(id);
            State s = diaryEntry.getState();
            s.saveDocument(diaryEntry);
            model.addAttribute("diaryEntry", diaryEntry);
        } catch(NullPointerException e) {
            // Recupera eventuale errore se non viene trovato il paziente nel database.
            message = e.getMessage();
            model.addAttribute("message", message);
        }
        catch (IllegalStateException e){
            // Recupera l'errore se si sta cercando di svolgere una azione non permessa
            // in base allo stato attuale in cui si trova la pagina di diario.
            message = e.getMessage();
            model.addAttribute("message", message);
        }
        // indirizzo pagina che verrà visualizzata
        return "diary/diaryEntryUpdateForm.html";
    }

    /**
     * Il metodo riceve le modifiche che l'utente ha svolto su una pagina di diario per poterle salvare nel database.
     * @param model oggetto che permette il passaggio dei dati al front end.
     * @param diaryEntryId identificativo della pagina di diario.
     * @param diaryEntry oggetto che contiene i dari provenienti dalla form.
     * @param diaryBindingResult oggetto che contiene gli errori di validazione.
     * @param user utente loggato.
     * @return se la validazione e il salvataggio su DB hanno successo, il sistema riporta alla pagina della form
     * del diario, con un messaggio di avvenuto corretto salvataggio dei nuovi dati (sia quelli modificati che quelli
     * rimasti uguali. Altrimenti viene mostrato un errore con i problemi riscontrati in fase di validazione o in fase
     * di verifica del controller/service.
     */
    @PutMapping(value="diary/updateDiaryEntryDraft/{id}")
    public String updateDiaryEntryDraft(Model model, @PathVariable("id") int diaryEntryId, @Valid @ModelAttribute("diaryEntry") DiaryEntry diaryEntry, BindingResult diaryBindingResult, Principal user){
        // Inizializzazione delle variabili utili
        String message = "";
        List<String> errorMessagesToShow = new ArrayList<>();
        String errorFromFromData = null;
        /*RACCOLTA DEGLI ERRORI DI VALIDAZIONE
        GLI ERRORI VENGONO MOSTRATI A SCHERMO TRAMITE LA LISTA errorMessagesToShow
        CHE RACCOGLIE IL CAMPO NON VALIDO (error.getField()) E IL MESSAGGIO DI ERRORE (error.getDefaultMessage())*/
        // Se ci sono errori di validazione //
        // 1. ERRORI RIFERITI AGLI ATTRIBUTI DELLA CLASSE UserRegistrationDto
        if(diaryBindingResult.hasErrors()) {
            // Per ogni errore di validazione trovato da BindingResult //
            for (ObjectError error : diaryBindingResult.getAllErrors()) {
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
            return "/diary/diaryEntryInfoMessage.html";
        }
        // CHECK SE E' POSSIBILE SALVARE COME DRAFT LA PAGINA DI DIARIO
        try {
            // Verifico che l'utente che ha modificato la pagina di diario sia il paziente che ha creato
            // la pagina di diario in primis.
            // 1. Ricerco il paziente nel database.
            Patient patient = customPatientDetailService.findPatientByUserUsername(user.getName());
            // 2. Ricerco la pagina di diario che corrisponde alla combinazione idPaziente - idPaginaDiDiario.
            DiaryEntry savedDiaryEntry = diaryEntryService.checkDiaryEntryUser(patient.getId(), diaryEntryId);
            // Verifico di poter modificare la pagina di diario, cioè verifico che sia ancora in stato bozza.
            State s = savedDiaryEntry.getState();
            s.uploadDocument(diaryEntry);
            // Verifico che la data non sia nel futuro.
            if(diaryEntry.getDate().after(new Date())){
                throw new ValidationException("The date is in the future");
            }
            // Inserisco il paziente nell'oggetto diaryEntry che sto modificando.
            diaryEntry.setPatient(patient);
            // Modifico la pagina di diario.
            diaryEntryService.findIdAndReplaceValues(diaryEntryId, diaryEntry);
            message = "The file related to " + diaryEntry.getDate() + "has been saved as a Draft";
            } catch(IllegalStateException e){
                message = e.getMessage();
                model.addAttribute("message", message);
                return "/diary/diaryEntryInfoMessage.html";
            } catch (NullPointerException e) {
                message = e.getMessage();
                model.addAttribute("message", message);
                return "/myErrorPage.html";
            } catch(ValidationException e ){
                errorFromFromData = e.getMessage();
                errorMessagesToShow.add(errorFromFromData);
                model.addAttribute("errorMessages", errorMessagesToShow);
            } catch (Exception e){
                // Eccezione perché non abbiamo trovato l'id della pagina di diario.
    //                message = "Id non trovato per il paziente loggato";
                // Eccezione se qualcosa va male nel caricamento nel DB.
    //                message = "Could not save the file related to date " + diaryEntry.getDate() + " or id " + diaryEntry.getId()+ "!";
                    message = e.getMessage();
                    model.addAttribute("message", message);
                    return "/diary/diaryEntryInfoMessage.html";
            }
            message = "Modifica avvenuta con successo!";
            model.addAttribute("message", message);
        // indirizzo pagina che verrà visualizzata
            return "diary/diaryEntryInfoMessage.html";
    }

    /**
     * Eliminazione della nota di diario. Possibile solo se la nota è ancora in stato bozza. La cancellazione è possibile
     * solo per il paziente che ha scritto la nota.
     * @param diaryEntryId identificativo della bozza.
     * @param user utente loggato (deve essere ROLE_PATIENT).
     * @param model oggetto che permette il passaggio dei dati al front end.
     * @return se la cancellazione è andata a buon fine, viene recuperato un messaggio di successo. Altrimenti viene
     * mostrato un errore e la cancellazione non avviene.
     */
    @DeleteMapping("diary/deleteDiaryEntry/{id}")
    public String deleteDraftDiaryEntry(@PathVariable("id") int diaryEntryId, Principal user, Model model){
        // Inizializzazione delle variabili utili
        String message = "";
        try {
            // Verifico che l'utente loggato sia un paziente e che la pagina di diario da eliminare sia del paziente
            // stesso.
            Patient patient = customPatientDetailService.findPatientByUserUsername(user.getName());
            DiaryEntry diaryEntry = diaryEntryService.checkDiaryEntryUser(patient.getId(), diaryEntryId);
            // Verifico di poter cancellare la pagina di diario (azione possibile solo se ancora in stato di bozza).
            State s = diaryEntry.getState();
            s.deleteDraft(diaryEntry);
            // Procedi alla cancellazione.
            diaryEntryService.deleteDraftDiaryEntry(diaryEntryId);
            message = "La pagina di diario è stata cancellata.";
            model.addAttribute("message", message);
        }
        catch (IllegalStateException e){
            // Recupera l'errore mostrato se la pagina di diario non può essere cancellata.
            message = e.getMessage();
            model.addAttribute("message", message);
            return "diary/diaryEntryInfoMessage.html";
        }  catch (NullPointerException e){
            // Recupera l'errore mostrato se il paziente non esiste nel db.
            message = e.getMessage();
            model.addAttribute("message", message);
            return "/myErrorPage.html";
        } catch (Exception e){
            message = e.getMessage();
            // Messaggi personalizzati da inserire:
/*            message = "Errore nella cancellazione della pagina di diario.";
            message = "Il record selezionato non appartiene all'utente loggato. Per il corretto funzionamento, procedi al logout e login";*/
            model.addAttribute("message", message);
            return "diary/diaryEntryInfoMessage.html";
        }
        // indirizzo pagina che verrà visualizzata
        return "diary/diaryEntryInfoMessage.html";
    }


}
