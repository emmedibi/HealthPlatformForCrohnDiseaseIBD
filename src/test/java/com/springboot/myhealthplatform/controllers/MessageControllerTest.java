package com.springboot.myhealthplatform.controllers;

import com.springboot.myhealthplatform.bean.Patient;
import com.springboot.myhealthplatform.bean.User;
import com.springboot.myhealthplatform.board.bean.Message;
import com.springboot.myhealthplatform.board.controller.MessageController;
import com.springboot.myhealthplatform.board.service.DiaryEntryService;
import com.springboot.myhealthplatform.board.service.MessageService;
import com.springboot.myhealthplatform.login.CustomUserDetailsService;
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
import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MessageController.class)
@AutoConfigureMockMvc
public class MessageControllerTest {

    @MockBean
    private MessageService messageService;
    @MockBean
    private CustomPatientDetailService customPatientDetailService;
    @MockBean
    private CustomDoctorDetailService customDoctorDetailService;
    @MockBean
    private CustomUserDetailsService customUserDetailsService;
    @MockBean
    private DiaryEntryService diaryEntryService;

    @Mock
    private Principal principal;
    private String defaultName = "patient";
    @Autowired
    MockMvc mockMvc;

    /** --SUPERATO --
     * Ritorna l'elenco dei messaggi per uno specifico utente, identificato con patientId.
     * @throws Exception
     */
    @Test
    @WithMockUser(username="patient",roles="PATIENT")
    public void it_should_return_list_of_patient_messages() throws Exception{
        int patientId = 1;
        Patient patient = new Patient();
        patient.setName("patientName");
        User user = new User();
        user.setId(2L);
        patient.setUser(user);
        Message mex1 = new Message();
        mex1.setMessageBody("messageOneBody");
        Message mex2 = new Message();
        mex2.setMessageBody("messageTwoBody");

        when(principal.getName()).thenReturn(defaultName);
        when(customPatientDetailService.findPatientByUserUsername(defaultName)).thenReturn(patient);

        when(messageService.findAllByUserId(patient.getUser().getId())).thenReturn(Arrays.asList(mex1, mex2));

        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/message/patientBoard"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("/message/patientMessageBoard.html"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("messages"))
                .andExpect(MockMvcResultMatchers.model().attribute("messages", Arrays.asList(mex1, mex2)));
    }

    /** -- SUPERATO --
     * Ritorna un messaggio di errore, lanciando ArrayIndexOutOfBoundsException
     * @throws Exception
     */
    @Test
    @WithMockUser(username="patient",roles="PATIENT")
    public void it_should_throw_an_exception() throws Exception {

        int patientId = 1;
        Patient patient = new Patient();
        patient.setName("patientName");
        User user = new User();
        user.setId(2L);
        patient.setUser(user);
        Message mex1 = new Message();
        mex1.setMessageBody("messageOneBody");
        Message mex2 = new Message();
        mex2.setMessageBody("messageTwoBody");

        when(principal.getName()).thenReturn(defaultName);
        when(customPatientDetailService.findPatientByUserUsername(defaultName)).thenReturn(patient);

        when(messageService.findAllByUserId(patient.getUser().getId())).thenThrow(new ArrayIndexOutOfBoundsException("Errore durante la ricerca dei messaggi"));

        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/message/patientBoard"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("/message/patientMessageBoard.html"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("messageAlert"));
    }

    /** --SUPERATO --
     * Ritorna un errore perché l'utente inserito non viene ritrovato nella tabella dei pazienti (essendo
     * un dottore - ROLE_DOCTOR).
     * @throws Exception
     */
    @Test
    @WithMockUser(username="doctor",roles="DOCTOR")
    public void it_should_return_error_because_user_not_authorized() throws Exception{
        int patientId = 1;
        Patient patient = new Patient();
        patient.setName("patientName");
        User user = new User();
        user.setId(2L);
        patient.setUser(user);
        Message mex1 = new Message();
        mex1.setMessageBody("messageOneBody");
        Message mex2 = new Message();
        mex2.setMessageBody("messageTwoBody");

        when(principal.getName()).thenReturn(defaultName);
        when(customPatientDetailService.findPatientByUserUsername(defaultName)).thenThrow(new NullPointerException("Errore durante la ricerca dei messaggi"));

        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/message/patientBoard"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("/message/patientMessageBoard.html"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("messageAlert"));
    }

}
