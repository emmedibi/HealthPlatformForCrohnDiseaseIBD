package com.springboot.myhealthplatform.controller;

import com.springboot.myhealthplatform.bean.Doctor;
import com.springboot.myhealthplatform.bean.Patient;
import com.springboot.myhealthplatform.repository.DoctorRepository;
import com.springboot.myhealthplatform.service.CustomDoctorDetailService;
import com.springboot.myhealthplatform.service.CustomPatientDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
/**
 * Controller relativo alle attività svolte dall'utente medico.
 */
@Controller //this class is a controller
@RequestMapping(path="/doctor")
public class DoctorController {

    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private CustomPatientDetailService customPatientDetailService;
    @Autowired
    private CustomDoctorDetailService customDoctorDetailService;


    /**
     * Verifica l'esistenza dello username come medico e restituisce la lista di pazienti del medico loggato.
     * @param user utente loggato
     * @param model modello da utilizzare per riportare le informazioni su Thymeleaf
     * @return lista di pazienti del medico loggato
     */
    @RequestMapping(value="/yourPatients", method = RequestMethod.GET)
    public String showDoctorPatients(Principal user, Model model){
        String message = "";
        try {
            Doctor doctor = customDoctorDetailService.findDoctorByUsername(user.getName());
            model.addAttribute("doctor", doctor);
            List<Patient> patients = customPatientDetailService.findAllByDoctorId(doctor.getId());
            model.addAttribute("patients", patients);
        } catch(Exception e){
            message = e.getMessage();
            model.addAttribute("message", message);
            model.addAttribute("/myErrorPage.html");
        }
        return "/doctor/yourPatients.html";
    }

}
