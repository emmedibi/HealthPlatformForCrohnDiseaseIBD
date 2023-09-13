package com.springboot.myhealthplatform.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.myhealthplatform.bean.Patient;
import com.springboot.myhealthplatform.bean.User;
import com.springboot.myhealthplatform.board.bean.DiaryEntry;
import com.springboot.myhealthplatform.board.controller.DiaryEntryController;
import com.springboot.myhealthplatform.board.controller.ExamCategoryController;
import com.springboot.myhealthplatform.board.service.DiaryEntryService;
import com.springboot.myhealthplatform.service.CustomDoctorDetailService;
import com.springboot.myhealthplatform.service.CustomPatientDetailService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.temporal.Temporal;
import java.util.Date;

import static java.lang.Boolean.valueOf;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DiaryEntryController.class)
@AutoConfigureMockMvc
@WithMockUser(username="patient", roles="PATIENT")
public class DiaryEntryControllerPostTest {

    @MockBean
    private DiaryEntryService diaryEntryService;
    @MockBean
    private CustomPatientDetailService customPatientDetailService;
    @MockBean
    private CustomDoctorDetailService customDoctorDetailService;
    @Autowired
    private MockMvc mockMvc;
    @Mock
    private Principal principal;
    @Mock
    private Patient patient;
    private String defaultName = "patient";
    @Mock
    private User user;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date appointmentDate = new Date();

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
 /*       // Senza il costruttore fatto in questo modo, examCategoryService è null
        DiaryEntryController controller = new DiaryEntryController(diaryEntryService, customPatientDetailService, customDoctorDetailService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();*/
    }

    @Test
    public void it_should_save_a_diary_entry() throws Exception {
        int patientId = 1;
        Patient patient = new Patient();
        patient.setName("patientName");
        patient.setId(patientId);
        User user = new User();
        user.setId(2L);
        patient.setUser(user);
        String date = new Date().toString();
        String newDateString;

//        Date d = sdf.parse(date);
//        sdf.applyPattern("yyyy/MM//dd");
//        newDateString = sdf.format(d);
        DiaryEntry diaryEntry1 = new DiaryEntry(patient, "test", appointmentDate, true, true, false, false, 2, "DRAFT", null);
        DiaryEntryController controller = new DiaryEntryController(diaryEntryService, customPatientDetailService, customDoctorDetailService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        when(customPatientDetailService.findPatientByUserUsername(defaultName)).thenReturn(patient);
        when(diaryEntryService.findByDateAndPatientId(diaryEntry1.getDate(), patient.getId())).thenReturn(null);

        mockMvc.perform(post("/diary/saveDiaryEntryAsDraft")
                        .param("note", diaryEntry1.getNote())
                        .param("date", "2023-06-17 00:00:00.000000")
                        .param("diarrhea", valueOf(diaryEntry1.isDiarrhea()).toString())
                        .param("fatigue", valueOf(diaryEntry1.isFatigue()).toString())
                        .param("abdominalPain", valueOf(diaryEntry1.isAbdominalPain()).toString())
                        .param("numberOfBowelMovement", String.valueOf(diaryEntry1.getNumberOfBowelMovement()))

                        .principal(principal))
                .andExpect(status().isOk())
                .andDo(print());
    }

    /**
     * Ho cambiato configurazione per i GET - non recupera comunque l'oggetto
     * @throws Exception
     */
    @Test
    @WithMockUser(username="patient", roles="PATIENT")
    public void it_should_modify_the_values_of_a_diary_entry() throws Exception {
        int patientId = 1;
        Patient patient = new Patient();
        patient.setName("patientName");
        User user = new User();
        user.setId(2L);
        patient.setUser(user);
        DiaryEntry diaryEntry = new DiaryEntry();
        diaryEntry.setEntryState("DRAFT");
        diaryEntry.setDiarrhea(false);
        diaryEntry.setNote("test");
        diaryEntry.setAbdominalPain(false);
        diaryEntry.setBloodStool(false);
        diaryEntry.setFatigue(false);
        diaryEntry.setNumberOfBowelMovement(1);
        diaryEntry.setDate(new Date());
        int diaryEntryId = 1;

        DiaryEntry diaryEntryModified = new DiaryEntry();
        diaryEntryModified.setDate(diaryEntry.getDate());
        diaryEntryModified.setNote("test");
        diaryEntryModified.setDiarrhea(true);
        diaryEntryModified.setFatigue(true);
        diaryEntryModified.setEntryState(diaryEntry.getEntryState());
        diaryEntryModified.setBloodStool(diaryEntry.isBloodStool());
        diaryEntryModified.setNumberOfBowelMovement(diaryEntryModified.getNumberOfBowelMovement());
        diaryEntryModified.setAbdominalPain(diaryEntryModified.isAbdominalPain());

        when(diaryEntryService.findIdAndReplaceValues(diaryEntryId, diaryEntryModified)).thenReturn(diaryEntryModified);
        when(diaryEntryService.checkDiaryEntryUser(patient.getId(), diaryEntryId)).thenReturn(diaryEntry);

        mockMvc.perform(put("/diary/updateDiaryEntryDraft/" + diaryEntry.getId())
                        .principal(principal)
                .param("note", diaryEntryModified.getNote())
                .param("diarrhea", valueOf(diaryEntryModified.isDiarrhea()).toString())
                .param("fatigue", valueOf(diaryEntryModified.isFatigue()).toString())
                .param("abdominalPain", valueOf(diaryEntryModified.isAbdominalPain()).toString())
                .param("numberOfBowelMovement", String.valueOf(diaryEntryModified.getNumberOfBowelMovement())))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
