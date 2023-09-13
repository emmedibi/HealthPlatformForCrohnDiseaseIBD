package com.springboot.myhealthplatform.controllers;

import com.springboot.myhealthplatform.bean.Doctor;
import com.springboot.myhealthplatform.bean.Patient;
import com.springboot.myhealthplatform.controller.NurseController;
import com.springboot.myhealthplatform.login.CustomUserDetailsService;
import com.springboot.myhealthplatform.service.CustomDoctorDetailService;
import com.springboot.myhealthplatform.service.CustomPatientDetailService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NurseController.class)
@AutoConfigureMockMvc
@WithMockUser(username="nurse",roles="NURSE")
public class NurseControllerTest {

    @MockBean
    private CustomDoctorDetailService customDoctorDetailService;
    @MockBean
    private CustomUserDetailsService customUserDetailsService;
    @MockBean
    private CustomPatientDetailService customPatientDetailService;
    @Autowired
    private MockMvc mockMvc;

    /** -- SUPERATO --
     * Verifica che vengano recuperati dal database tutti gli utenti dottori registrati
     * @throws Exception
     */
    @Test
    public void it_should_return_list_of_doctors() throws Exception {
        Doctor doctor1 = new Doctor();
        Doctor doctor2 = new Doctor();
        doctor1.setName("doctorOneName");
        doctor2.setName("doctorTwoName");

        when(customDoctorDetailService.getAll()).thenReturn(Arrays.asList(doctor1, doctor2));

        mockMvc.perform(MockMvcRequestBuilders.get("/nurse/listOfDoctors"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("/nurse/listOfDoctors.html"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("doctors"));
    }

    /** -- SUPERATO --
     * Verifica che ritorni la lista di tutti i pazienti registrati nel database.
     * @throws Exception
     */
    @Test
    public void it_should_return_list_of_patients() throws Exception {
        Patient patient1 = new Patient();
        Patient patient2 = new Patient();
        patient1.setName("patientOneName");
        patient2.setName("patientTwoName");

        when(customPatientDetailService.getAll()).thenReturn(Arrays.asList(patient1, patient2));

        mockMvc.perform(MockMvcRequestBuilders.get("/nurse/listOfPatients"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("/nurse/listOfPatients.html"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("patients"));

    }
}
