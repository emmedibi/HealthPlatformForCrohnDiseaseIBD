package com.springboot.myhealthplatform.controllers;

import com.springboot.myhealthplatform.bean.Patient;
import com.springboot.myhealthplatform.bean.User;
import com.springboot.myhealthplatform.board.bean.DiaryEntry;
import com.springboot.myhealthplatform.board.controller.DiaryEntryController;
import com.springboot.myhealthplatform.board.service.DiaryEntryService;
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
@SpringBootTest(classes = DiaryEntryController.class)
@AutoConfigureMockMvc
public class DiaryEntryControllerTest {

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
    private String defaultName = "patient";

/*    @Before
    public void setUp() throws Exception {
        initMocks(this);
        DiaryEntryController controller = new DiaryEntryController();
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }*/

    /** -- NON LEGGE L'OGGETTO CHE HO INSERITO --
     * Verifica che il metodo POST di invio al database dell'oggetto diaryEntry funzioni correttamente.
     * @throws Exception
     */


    /** -- SUPERATO --
     * Verifica che venga recuperata la lista delle note del paziente loggato.
     * @throws Exception
     */
    @Test
    @WithMockUser(username="patient", roles="PATIENT")
    public void it_should_return_diary_entries() throws Exception {
        int patientId = 1;
        Patient patient = new Patient();
        patient.setName("patientName");
        User user = new User();
        user.setId(2L);
        patient.setUser(user);
        DiaryEntry diaryEntry1 = new DiaryEntry();
        diaryEntry1.setNote("diaryEntry note 1");
        DiaryEntry diaryEntry2 = new DiaryEntry();
        diaryEntry2.setNote("diaryEntry note 2");
        DiaryEntry diaryEntryDraft1 = new DiaryEntry();
        diaryEntry1.setNote("diaryEntry note 1D");
        DiaryEntry diaryEntryDraft2 = new DiaryEntry();
        diaryEntry2.setNote("diaryEntry note 2D");

        when(principal.getName()).thenReturn(defaultName);
        when(customPatientDetailService.findPatientByUserUsername(defaultName)).thenReturn(patient);

        when(diaryEntryService.getAllPublishedDiaryEntries(patient.getId())).thenReturn(Arrays.asList(diaryEntry1, diaryEntry2));
        when(diaryEntryService.getAllDraftDiaryEntries(patient.getId())).thenReturn(Arrays.asList(diaryEntryDraft1, diaryEntryDraft2));
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/diary/diaryEntries"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("diary/diaryEntries.html"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("diaryEntryDrafts"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("diaryEntries"));

    }

    /** -- SUPERATO --
     * Verifica che, dato l'id di un paziente, ritorna una lista di pagine di diario.
     * @throws Exception
     */
    @Test
    @WithMockUser(username="nurse", roles="NURSE")
    public void it_return_diary_entries_of_a_patient_for_nurse() throws Exception {

        int patientId = 1;
        Patient patient = new Patient();
        patient.setName("patientName");
        patient.setId(patientId);
        User user = new User();
        user.setId(2L);
        patient.setUser(user);
        DiaryEntry diaryEntry1 = new DiaryEntry();
        diaryEntry1.setNote("diaryEntry note 1");
        DiaryEntry diaryEntry2 = new DiaryEntry();
        diaryEntry2.setNote("diaryEntry note 2");
        DiaryEntry diaryEntryDraft1 = new DiaryEntry();
        diaryEntry1.setNote("diaryEntry note 1D");
        DiaryEntry diaryEntryDraft2 = new DiaryEntry();
        diaryEntry2.setNote("diaryEntry note 2D");


        when(diaryEntryService.getAllPublishedDiaryEntries(patient.getId())).thenReturn(Arrays.asList(diaryEntry1, diaryEntry2));

        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/nurse/patientDiary/patientDiaryEntries/" + patient.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("nurse/patientDiary/patientDiaryEntriesList.html"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("diaryEntries"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("patientId"));

    }

}
