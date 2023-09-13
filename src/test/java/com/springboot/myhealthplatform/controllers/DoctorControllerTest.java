package com.springboot.myhealthplatform.controllers;

import com.springboot.myhealthplatform.bean.Doctor;
import com.springboot.myhealthplatform.bean.Patient;
import com.springboot.myhealthplatform.bean.User;
import com.springboot.myhealthplatform.controller.DoctorController;
import com.springboot.myhealthplatform.repository.DoctorRepository;
import com.springboot.myhealthplatform.service.CustomDoctorDetailService;
import com.springboot.myhealthplatform.service.CustomPatientDetailService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = DoctorController.class)
@AutoConfigureMockMvc
@WithMockUser(username="doctor",roles="DOCTOR")
public class DoctorControllerTest {

    @MockBean
    private CustomDoctorDetailService customDoctorDetailService;
    @MockBean
    private CustomPatientDetailService customPatientDetailService;
    @MockBean
    private DoctorRepository doctorRepository;
    @Autowired
    MockMvc mockMvc;
    @Mock
    private Principal principal;
    private String defaultName = "doctor";
    @Mock
    private User doctorUser;

    private Doctor doctor;

    private List<Patient> patients= new ArrayList<>();
    private int doctorId = 102;

    @Test
    public void showDoctorPatientsWithPrefilledData() throws Exception{
        int doctor1Id = 1;
        Doctor doctor1 = new Doctor();
        doctor1.setName("doctorName");
        doctor1.setId(1);
        Patient patient1 = new Patient();
        Patient patient2 = new Patient();
        patient1.setName("patient1Name");
        patient2.setName("patient2Name");

        when(principal.getName()).thenReturn(defaultName);
        when(customDoctorDetailService.findDoctorByUsername(defaultName)).thenReturn(doctor1);
        when(customPatientDetailService.findAllByDoctorId(doctor1Id)).thenReturn(Arrays.asList(patient1, patient2));

        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/doctor/yourPatients"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("/doctor/yourPatients.html"))
                .andExpect(MockMvcResultMatchers.model().attribute("doctor", doctor1))
                .andExpect(MockMvcResultMatchers.model().attributeExists("patients"));
    }


}
