package com.springboot.myhealthplatform.board.controller;

import com.springboot.myhealthplatform.board.bean.ExamCategory;
import com.springboot.myhealthplatform.board.service.ExamCategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;
/**
 * Classe Controller riceve gli user input relativi agli appuntamenti (classe ExamCategory),
 * li elabora e invia la richiesta al Service opportuno; allo stesso tempo riceve le informazioni
 * richieste dal Service e le elabora e le invia all'interfaccia utente.
 */
@Controller
public class ExamCategoryController {
    @Autowired
    private ExamCategoryService examCategoryService;

    public ExamCategoryController(ExamCategoryService examCategoryService) {
        this.examCategoryService = examCategoryService;
    }


    /**
     * Aggiunge la categoria di esame (ExamCategory) dopo aver validato i campi provenienti dalla
     * form.
     * @param model oggetto che permette il passaggio dei dati al front end.
     * @param examCategory oggetto che contiene i dati ricavati dalla form.
     * @return la pagina html della form con il messaggio di successo di salvataggio oppure
     * un messaggio di errore.
     */
    @PostMapping("admin/examCategories/addCategory")
    public String addCategory(Model model, @Valid @ModelAttribute("examCategory") ExamCategory examCategory, BindingResult examCategoryBindingResult){
        // Inizializzazione delle variabili utili.
        String message;
        List<String> errorMessagesToShow = new ArrayList<>();
        String errorFromFromData = null;
        if(examCategoryBindingResult.hasErrors()){
            for (ObjectError error : examCategoryBindingResult.getAllErrors()) {
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
            return "/admin/examCategories/examCategoryForm.html";
        }
        try {
            examCategoryService.save(examCategory);
            message = "Categoria salvata con successo";
        } catch (Exception e){
            message = e.getMessage();
        }
        model.addAttribute("message", message);
        return "admin/examCategories/examCategoryForm.html";
    }
}
