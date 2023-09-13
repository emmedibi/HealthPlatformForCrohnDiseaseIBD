package com.springboot.myhealthplatform.controller;


import com.springboot.myhealthplatform.bean.Doctor;
import com.springboot.myhealthplatform.bean.User;
import com.springboot.myhealthplatform.login.CustomUserDetailsService;
import com.springboot.myhealthplatform.service.CustomDoctorDetailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller relativo alle attività svolte dall'utente Admin.
 */
@Controller
public class AdminUserController {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private CustomDoctorDetailService customDoctorDetailService;

    /**
     * Recupera tutti gli utenti tramite il loro User e li mostra a schermo all'utente Admin
     * @param model classe che permette di recuperare messaggi o oggetti, inviarli al FrontEnd che poi li manipolerà in
     *              base alle sue necessità.
     * @return pagina di Front End "showAllUsers" dove verranno mostrati i dati recuperati.
     */
    @RequestMapping(value="/admin/showAllUsers", method=RequestMethod.GET)
    public String showAllTheUsers(Model model) { // @ModelAttribute added
        List<User> users = new ArrayList<User>();
        try{
            users= customUserDetailsService.getAll();
            model.addAttribute("users", users); // add this line to your code
        } catch(Exception e){
            String message = "Errore nel recupero dei dati";
            model.addAttribute("message", message);
            return "/myErrorPage.html";
        }
            return "/admin/showAllUsers.html";
    }

    /**
     * Metodo chiamato in fase di registrazione di un utente di tipo Patient.
     * Il metodo recupera i dati di tutti i dottori dell'istituto in modo che possa esserne scelto uno (solo uno) da
     * associare al paziente che si sta creando.
     * Se non dovessero esserci medici registrati, la lista "List<Doctor>" risulterebbe uguale a "null". Questa condizione
     * viene verificata e se vera, viene lanciata una eccezione "NullPointerException" che porta all'interruzione del metodo
     * e a mostrare nella pagina "myErrorPage.html" un messaggio di errore.
     * Se dovessero esserci problemi nel recupero dei dati dal DB, il sistema mostrerà un messaggio di errore nella pagina
     * "myErrorPage.html".
     * @param model
     * @return la pagina FrontEnd dove si procederà alla creazione del paziente e verrà mostrata la lista dei medici.
     */
    @RequestMapping(value="/admin/patientRegistrationPage", method=RequestMethod.GET)
    public String showPatientRegistrationPage(Model model) { // @ModelAttribute added
        String message = "";
        try{
            // Recupera la lista di medici da abbinare al paziente che si sta registrando.
            List<Doctor> doctors = new ArrayList<Doctor>();
            doctors= customDoctorDetailService.getAll();
            if(doctors == null){
                throw new NullPointerException("Nessun medico registrato. Procedere a registrare un medico prima di registrare un paziente");
            }
            model.addAttribute("doctors", doctors); // add this line to your code
        } catch(NullPointerException e){
            message = e.getMessage();
            model.addAttribute("message", message);
            return "/myErrorPage.html";
        }   catch(Exception e){
            message = "Errore nel recupero dei dati";
            model.addAttribute("message", message);
            return "/myErrorPage.html";
        }
        return "/admin/patientRegistration.html";
    }

}
