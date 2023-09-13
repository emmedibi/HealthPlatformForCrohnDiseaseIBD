package com.springboot.myhealthplatform.controller;

import com.springboot.myhealthplatform.bean.Doctor;
import com.springboot.myhealthplatform.bean.Patient;
import com.springboot.myhealthplatform.login.CustomUserDetailsService;
import com.springboot.myhealthplatform.service.CustomDoctorDetailService;
import com.springboot.myhealthplatform.service.CustomPatientDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * Controller relativo alle attività svolte dall'utente infermiere.
 */
@Controller
@RequestMapping("/nurse")
public class NurseController  {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private CustomPatientDetailService customPatientDetailService;
    @Autowired
    private CustomDoctorDetailService customDoctorDetailService;

    /**
     * Recupera tutti i pazienti presenti nel database.
     * @param model oggetto che permette di portare dati nel frontEnd e, lì, manipolarli.
     * @return la pagina html dove verranno mostrati tutti i pazienti.
     */
    @RequestMapping(value="/listOfPatients", method=RequestMethod.GET)
    public String showAllThePatients(Model model) {
        try {
            List<Patient> patients = customPatientDetailService.getAll();
            model.addAttribute("patients", patients); // add this line to your code
        } catch (Exception e){
            String message = e.getMessage();
            model.addAttribute("message", message);
            return "/myErrorPage.html";
        }
        return "/nurse/listOfPatients.html";
    }

    /**
     * Recupera tutti i dottori presenti nel database.
     * @param model oggetto che permette di portare dati nel frontEnd e, lì, manipolarli.
     * @return la pagina html dove verranno mostrati tutti i dottori.
     */
    @RequestMapping(value="/listOfDoctors", method=RequestMethod.GET)
    public String showListOfDoctors(Model model) {
        try {
            List<Doctor> doctors = customDoctorDetailService.getAll();
            model.addAttribute("doctors", doctors); // add this line to your code
        } catch (Exception e){
            String message = e.getMessage();
            model.addAttribute("message", message);
            return "/myErrorPage.html";
        }
        return "/nurse/listOfDoctors.html";
    }

}
