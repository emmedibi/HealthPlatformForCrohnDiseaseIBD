package com.springboot.myhealthplatform.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.myhealthplatform.bean.Patient;
import com.springboot.myhealthplatform.bean.User;
import com.springboot.myhealthplatform.board.bean.BloodTestReport;
import com.springboot.myhealthplatform.board.controller.BloodTestReportController;
import com.springboot.myhealthplatform.board.service.BloodTestReportService;
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
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;
import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BloodTestReportController.class)
@AutoConfigureMockMvc
public class BloodTestReportControllerTest {


    @MockBean
    private BloodTestReportService bloodTestReportService;
    @MockBean
    private CustomPatientDetailService customPatientDetailService;
    @Autowired
    MockMvc mockMvc;
    @Mock
    private Principal principal;

    private String defaultName;

    @Autowired
    private WebApplicationContext webApplicationContext;
    @MockBean
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username="patient",roles="PATIENT")
    public void it_should_return_list_of_blood_exams_of_logged_patient() throws Exception {
        defaultName = "patient";
        int patientId = 1;
        // Esempio di esami del sangue
        BloodTestReport bloodTestReport1 = new BloodTestReport();
        BloodTestReport bloodTestReport2 = new BloodTestReport();
        bloodTestReport1.setDescription("esami 2023");
        bloodTestReport2.setDescription("esami 2022");
        // Paziente loggato
        Patient patient = new Patient();
        // Utente riferito al paziente loggato
        User user = new User();
        user.setUsername(defaultName);
        patient.setUser(user);
        patient.setId(patientId);

        when(principal.getName()).thenReturn(defaultName);
        when(customPatientDetailService.findPatientByUserUsername(defaultName)).thenReturn(patient);

        when(bloodTestReportService.getAllBloodTestReports(patientId)).thenReturn(Arrays.asList(bloodTestReport1, bloodTestReport2));

        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/reports/bloodExams"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("/reports/bloodExams.html"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("bloodTestReports"));

    }

}
