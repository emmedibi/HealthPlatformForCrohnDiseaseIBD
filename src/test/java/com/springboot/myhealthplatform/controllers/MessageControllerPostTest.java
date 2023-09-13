package com.springboot.myhealthplatform.controllers;

import com.springboot.myhealthplatform.bean.Doctor;
import com.springboot.myhealthplatform.bean.Patient;
import com.springboot.myhealthplatform.bean.User;
import com.springboot.myhealthplatform.board.bean.Message;
import com.springboot.myhealthplatform.board.controller.MessageController;
import com.springboot.myhealthplatform.board.service.DiaryEntryService;
import com.springboot.myhealthplatform.board.service.MessageService;
import com.springboot.myhealthplatform.login.CustomUserDetailsService;
import com.springboot.myhealthplatform.service.CustomDoctorDetailService;
import com.springboot.myhealthplatform.service.CustomPatientDetailService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MessageController.class)
@AutoConfigureMockMvc
public class MessageControllerPostTest {

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

    private MockMvc mockMvc;
    @Mock
    private Principal user;
    private String defaultName;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        // Senza il costruttore fatto in questo modo, examCategoryService è null
        MessageController controller = new MessageController(messageService, customPatientDetailService, customDoctorDetailService, customUserDetailsService, diaryEntryService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    /**
     * Verifica funzionamento salvataggio di un nuovo messaggio da parte del paziente.
     * @throws Exception
     */
    @Test
    public void it_should_save_a_message() throws Exception {

        Patient patient = new Patient();
        Doctor doctor = new Doctor();
        doctor.setName("doctorName");
        doctor.setSurname("doctorSurname");
        patient.setDoctor(doctor);
        Message mex = new Message();
        mex.setMessageBody("MessaggioTest");
        mex.setReadMessage(false);
        mex.setRecipient(new User());

        when(customPatientDetailService.findPatientByUserUsername(user.getName())).thenReturn(patient);
        mex.setSender(patient.getUser());
        when(messageService.save(mex)).thenReturn(mex);

        this.mockMvc
                .perform(post("/message/addANewMessage")
                        .param("messageBody", mex.getMessageBody())
                        .principal(user)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    /** -- SUPERATO --
     * Lancia un errore di validazione quando l'attributo message.messageBody ha lunghezza minore di 2.
     * @throws Exception
     */
    @Test
    public void it_should_throw_validation_error_because_messageBody_is_too_short() throws Exception {

        Patient patient = new Patient();
        Doctor doctor = new Doctor();
        doctor.setName("doctorName");
        doctor.setSurname("doctorSurname");
        patient.setDoctor(doctor);
        Message mex = new Message();
        mex.setMessageBody("M");
        mex.setReadMessage(false);
        mex.setRecipient(new User());

        when(customPatientDetailService.findPatientByUserUsername(user.getName())).thenReturn(patient);
        mex.setSender(patient.getUser());
        when(messageService.save(mex)).thenReturn(mex);

        this.mockMvc
                .perform(post("/message/addANewMessage")
                        .param("messageBody", mex.getMessageBody())
                        .principal(user)
                )
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("/message/NewMessageForm.html"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("errorMessages"))
                .andDo(print());
    }

    /** -- SUPERATO --
     * Verifica lancio errore se l'utente Patient non ha un medico associato (patient.getDoctor() == null)
     * @throws Exception
     */
    @Test
    public void it_should_throw_exception_error_because_doctor_s_user_is_null() throws Exception {

        Patient patient = new Patient();
        patient.setDoctor(null);
        Message mex = new Message();
        mex.setMessageBody("MessageBodyTest");
        mex.setReadMessage(false);
        mex.setRecipient(new User());

        when(customPatientDetailService.findPatientByUserUsername(user.getName())).thenReturn(patient);
        mex.setSender(patient.getUser());
        when(messageService.save(mex)).thenReturn(mex);

        this.mockMvc
                .perform(post("/message/addANewMessage")
                        .param("messageBody", mex.getMessageBody())
                        .principal(user)
                )
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("/message/NewMessageForm.html"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("messageAlert"))
                .andDo(print());
    }

/*    @Test
    public void it_should_save_a_message_from_doctor() throws Exception {

        // Paziente che ha inviato il messaggio
        Patient patient = new Patient();
        List<Role> patientRoles = new ArrayList<>();
        Role patientRole = new Role("ROLE_PATIENT");
        patientRoles.add(patientRole);
        User patientUser = new User("patientUser", "patientName", "patientSurname", "patientEmail", "patientPassword", patientRoles);
        patient.setId(1);
        patient.setName("namePatient");
        patient.setSurname("surnamePatient");
        patient.setCityOfBirth("Torino");
        patient.setCF("DBLMHL92R63F952C");
        patient.setAddress("via Pace");
        patient.setTelephone("0456789654");
        patient.setDOB(LocalDate.ofEpochDay(1967-01-01));
        Doctor doctor = new Doctor();
        doctor.setName("doctorName");
        doctor.setSurname("doctorSurname");
        patient.setDoctor(doctor);
        List<Role> roles = new ArrayList<>();
        Role role = new Role("ROLE_DOCTOR");
        roles.add(role);
        User doctorUser = new User(user.getName(), "doctorName", "doctorSurname", "doctorEmail", "doctorPassword", roles);
        doctor.setUser(doctorUser);
        Message mex = new Message();
        mex.setMessageBody("MessaggioTest");
        mex.setReadMessage(false);
        mex.setRecipient(new User());
        Message patientMessage = new Message();
        patientMessage.setId(3);
        System.out.println(patientMessage.getId());
        patientMessage.setMessageBody("PatientMessageBodyTest");


        patientMessage.setSender(patientUser);

        when(customDoctorDetailService.findDoctorByUsername(user.getName())).thenReturn(doctor);
        when(messageService.checkRecipientMessage(patientMessage.getId(), doctor.getUser())).thenReturn(patientMessage);
        System.out.println("PatientMessage: " + patientMessage.getMessageBody());
        when(messageService.save(mex)).thenReturn(mex);

        this.mockMvc
                .perform(post("/doctor/message/sendAMessageTo/" + patient.getId())
                        .param("messageBody", mex.getMessageBody())
                        .principal(user)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }*/


}
