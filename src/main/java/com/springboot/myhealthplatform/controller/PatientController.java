package com.springboot.myhealthplatform.controller;

import com.springboot.myhealthplatform.bean.Doctor;
import com.springboot.myhealthplatform.bean.Patient;
import com.springboot.myhealthplatform.repository.PatientRepository;
import com.springboot.myhealthplatform.service.CustomDoctorDetailService;
import com.springboot.myhealthplatform.service.CustomPatientDetailService;
import jakarta.validation.constraints.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;
/**
 * Controller relativo alle attività svolte dall'utente paziente.
 */
@Controller
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    PatientRepository patientRepository;
    @Autowired
    private CustomPatientDetailService customPatientDetailService;
    @Autowired
    private CustomDoctorDetailService customDoctorDetailService;

    /**
     * Recupera i dati del paziente loggato a sistema.
     * @param user sono i dati dell'utente loggato a sistema.
     * @param model oggetto che ci permette di portare i dati all'interfaccia di front end per poi
     *              manipolarli.
     * @return se presente restituisce l'oggetto Patient.
     */
    @RequestMapping(value="/personalData", method= RequestMethod.GET)
    public String showPersonalData(Principal user, Model model) {
        try {
            // Verifica che l'utente esista nel database o che l'utente loggato sia un paziente.
            Patient patient = customPatientDetailService.findPatientByUserUsername(user.getName());
            model.addAttribute("patient", patient);
            Doctor doctor = patient.getDoctor();
            if(doctor == null){
                throw new NullPointerException("Nessun medico collegato a questo paziente");
            }
            model.addAttribute("doctor", doctor);
        } catch(NullPointerException e){
            String message = e.getMessage();
            model.addAttribute("message", message);
            return "/myErrorPage.html";
        } catch(Exception e){
            String message = e.getMessage();
            model.addAttribute("message", message);
            return "/myErrorPage.html";
        }
        return "/patient/PersonalData.html";
    }



}
