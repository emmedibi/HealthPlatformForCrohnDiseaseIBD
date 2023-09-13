package com.springboot.myhealthplatform.controllers;

import com.springboot.myhealthplatform.bean.Doctor;
import com.springboot.myhealthplatform.bean.Patient;
import com.springboot.myhealthplatform.board.bean.Appointment;
import com.springboot.myhealthplatform.board.bean.ExamCategory;
import com.springboot.myhealthplatform.board.controller.AppointmentController;
import com.springboot.myhealthplatform.board.service.AppointmentService;
import com.springboot.myhealthplatform.board.service.EmailSenderService;
import com.springboot.myhealthplatform.board.service.ExamCategoryService;
import com.springboot.myhealthplatform.service.CustomDoctorDetailService;
import com.springboot.myhealthplatform.service.CustomPatientDetailService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppointmentController.class)
@AutoConfigureMockMvc

public class AppointmentControllerTest {

    @MockBean
    private AppointmentService appointmentService;
    @MockBean
    private CustomDoctorDetailService customDoctorDetailService;
    @MockBean
    private CustomPatientDetailService customPatientDetailService;
    @MockBean
    private ExamCategoryService examCategoryService;
    @MockBean
    private EmailSenderService emailSenderService;
    private MockMvc mockMvc;
    @Mock
    private Principal user;
    private String defaultName;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        // Senza il costruttore fatto in questo modo, examCategoryService è null
        AppointmentController controller = new AppointmentController(appointmentService, customDoctorDetailService, customPatientDetailService, examCategoryService, emailSenderService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @WithMockUser(username="patient",roles="PATIENT")
    public void it_should_add_an_appointment() throws Exception {
        ExamCategory eC = new ExamCategory("CategoriaTest");
        Patient patient = new Patient();
        Appointment app1 = new Appointment();
        LocalDateTime today = LocalDateTime.now();
        // calcolo una data per il primo appuntamento
        LocalDateTime appointment1FutureDate = LocalDateTime.of(today.getYear()+1, today.getMonth(), today.getDayOfMonth(), today.getHour(), today.getMinute(), today.getSecond());
        app1.setAppointmentDate(appointment1FutureDate);
        app1.setText("Visita");
        app1.setPatient(patient);
        app1.setExamCategory(eC);

        when(customPatientDetailService.findPatientByUserUsername(user.getName())).thenReturn(patient);

        when(appointmentService.save(app1)).thenReturn(app1);

        this.mockMvc
                .perform(post("/agenda/addAnAppointment")
                        .param("appointmentDate", String.valueOf(app1.getAppointmentDate()))
                        .param("text", app1.getText())
                                .principal(user)
                        )
                .andExpect(status().isOk())
                .andDo(print());
    }

    /** -- SUPERATO --
     * Verifico che, inserendo l'attributo appointment.text come stringa di un carattere, il sistema
     * blocchi il salvataggio dell'appuntamento perché l'attributo deve avere almeno due caratteri.
     * La verifica di validazione dell'input blocca l'esecuzione del controller e inserisce l'errore
     * nella List<String> errorMessages e lo mostra a schermo nella pagina agenda/agendaMessage.html.
     * @throws Exception
     */
    @Test
    @WithMockUser(username="patient",roles="PATIENT")
    public void it_should_not_add_appointment_because_title_is_empty() throws Exception {
        ExamCategory eC = new ExamCategory("CategoriaTest");
        Patient patient = new Patient();
        Appointment app1 = new Appointment();
        LocalDateTime today = LocalDateTime.now();
        // calcolo una data per il primo appuntamento
        LocalDateTime appointment1FutureDate = LocalDateTime.of(today.getYear()+1, today.getMonth(), today.getDayOfMonth(), today.getHour(), today.getMinute(), today.getSecond());
        app1.setAppointmentDate(appointment1FutureDate);
        app1.setText("V");
        app1.setPatient(patient);
        app1.setExamCategory(eC);

        when(customPatientDetailService.findPatientByUserUsername(user.getName())).thenReturn(patient);

        when(appointmentService.save(app1)).thenReturn(app1);

        this.mockMvc
                .perform(post("/agenda/addAnAppointment")
                        .param("appointmentDate", String.valueOf(app1.getAppointmentDate()))
                        .param("text", app1.getText())
                        .principal(user)
                )
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("agenda/agendaMessage"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("errorMessages"))
                .andDo(print());
    }

    @Test
    @WithMockUser(username="doctor",roles="DOCTOR")
    public void it_should_add_an_appointment_for_doctor_logged_in() throws Exception {
        ExamCategory eC = new ExamCategory("CategoriaTest");
        Patient patient = new Patient();
        Doctor doctor = new Doctor();
        Appointment app1 = new Appointment();
        LocalDateTime today = LocalDateTime.now();
        // calcolo una data per il primo appuntamento
        LocalDateTime appointment1FutureDate = LocalDateTime.of(today.getYear()+1, today.getMonth(), today.getDayOfMonth(), today.getHour(), today.getMinute(), today.getSecond());
        app1.setAppointmentDate(appointment1FutureDate);
        app1.setText("Visita");
        app1.setPatient(patient);
        app1.setExamCategory(eC);
        app1.setDoctor(doctor);

        when(customDoctorDetailService.findDoctorByUsername(user.getName())).thenReturn(doctor);

        when(appointmentService.save(app1)).thenReturn(app1);

        this.mockMvc
                .perform(post("/doctorAgenda/addDoctorAppointment")
                        .param("appointmentDate", String.valueOf(app1.getAppointmentDate()))
                        .param("text", app1.getText())
                        .principal(user)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    /** -- SUPERATO --
     * Verifica che per l'utente con ruolo DOCTOR, venga restituito l'elenco di tutti gli appuntamenti
     * dei suoi pazienti.
     * @throws Exception
     */
    @Test
    @WithMockUser(username="doctor",roles="DOCTOR")
    public void it_should_return_all_patient_appointments() throws Exception {
        ExamCategory eC = new ExamCategory("CategoriaTest");
        Patient patient = new Patient();
        Doctor doctor = new Doctor();
        doctor.setId(1);
        Appointment app1 = new Appointment();
        LocalDateTime today = LocalDateTime.now();
        // calcolo una data per il primo appuntamento
        LocalDateTime appointment1FutureDate = LocalDateTime.of(today.getYear()+1, today.getMonth(), today.getDayOfMonth(), today.getHour(), today.getMinute(), today.getSecond());
        app1.setAppointmentDate(appointment1FutureDate);
        app1.setText("Visita");
        app1.setPatient(patient);
        app1.setExamCategory(eC);
        app1.setDoctor(doctor);
        Appointment app2 = new Appointment();
        // calcolo una data per il primo appuntamento
        LocalDateTime appointment2FutureDate = LocalDateTime.of(today.getYear()+2, today.getMonth(), today.getDayOfMonth(), today.getHour(), today.getMinute(), today.getSecond());
        app2.setAppointmentDate(appointment2FutureDate);
        app2.setText("Visita Secondo appuntamento");
        app2.setPatient(patient);
        app2.setExamCategory(eC);
        app2.setDoctor(doctor);

        when(customDoctorDetailService.findDoctorByUsername(user.getName())).thenReturn(doctor);

        when(appointmentService.getAllAppointmentByDoctorId(doctor.getId())).thenReturn(Arrays.asList(app1, app2));

        this.mockMvc
                .perform(get("/doctorAgenda/doctorAgenda")
                        .principal(user))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("/doctor/doctorAgenda/doctorAppointmentList.html"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("appointments"))
                .andDo(print());
    }


    /** -- SUPERATO --
     * Verifica che venga lanciato un errore, in quanto l'utente che accede alla pagina è
     * un utente paziente (ruolo ="ROLE_PATIENT") e non ruolo medico ("ROLE_DOCTOR")
     * @throws Exception
     */
    @Test
    @WithMockUser(username="patient",roles="PATIENT")
    public void it_should_return_an_exception_because_user_is_not_doctor() throws Exception {
        ExamCategory eC = new ExamCategory("CategoriaTest");
        Patient patient = new Patient();
        Doctor doctor = new Doctor();
        doctor.setId(1);
        Appointment app1 = new Appointment();
        LocalDateTime today = LocalDateTime.now();
        // calcolo una data per il primo appuntamento
        LocalDateTime appointment1FutureDate = LocalDateTime.of(today.getYear()+1, today.getMonth(), today.getDayOfMonth(), today.getHour(), today.getMinute(), today.getSecond());
        app1.setAppointmentDate(appointment1FutureDate);
        app1.setText("Visita");
        app1.setPatient(patient);
        app1.setExamCategory(eC);
        app1.setDoctor(doctor);
        Appointment app2 = new Appointment();
        // calcolo una data per il primo appuntamento
        LocalDateTime appointment2FutureDate = LocalDateTime.of(today.getYear()+2, today.getMonth(), today.getDayOfMonth(), today.getHour(), today.getMinute(), today.getSecond());
        app2.setAppointmentDate(appointment2FutureDate);
        app2.setText("Visita Secondo appuntamento");
        app2.setPatient(patient);
        app2.setExamCategory(eC);
        app2.setDoctor(doctor);

        when(customDoctorDetailService.findDoctorByUsername(user.getName())).thenThrow(new NullPointerException("Nessun utente con ruolo medico trovato con questo username"));

        when(appointmentService.getAllAppointmentByDoctorId(doctor.getId())).thenReturn(Arrays.asList(app1, app2));

        this.mockMvc
                .perform(get("/doctorAgenda/doctorAgenda")
                        .principal(user))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("/myErrorPage.html"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("message"))
                .andDo(print());
    }

    /** -- SUPERATO --
     * Verifica che vengano recuperati i pazienti associati al paziente, prima di mostrare la
     * form di creazione di un appuntamento da parte del medico
     * @throws Exception
     */
    @Test
    @WithMockUser(username="doctor",roles="DOCTOR")
    public void it_returns_all_doctor_s_patients() throws Exception {
        Doctor doctor = new Doctor();
        doctor.setId(1);
        Patient patient1 = new Patient();
        patient1.setDoctor(doctor);
        Patient patient2 = new Patient();
        patient2.setDoctor(doctor);
        Patient patient3 = new Patient();
        patient3.setDoctor(doctor);
        doctor.setPatients(Arrays.asList(patient1, patient2, patient3));

        when(customDoctorDetailService.findDoctorByUsername(user.getName())).thenReturn(doctor);

        this.mockMvc
                .perform(get("/doctorAgenda/doctorAppointmentForm")
                        .principal(user))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("/doctor/doctorAgenda/doctorAppointmentForm.html"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("patients"))
                .andDo(print());

    }

    /** -- SUPERATO --
     * Verifica che vengano mostrati all'infermiere loggato gli appuntamenti di oggi per uno specifico dottore
     * @throws Exception
     */
    @Test
    @WithMockUser(username="nurse",roles="NURSE")
    public void it_returns_appointments_of_a_doctor() throws Exception {
        ExamCategory eC = new ExamCategory("CategoriaTest");
        Patient patient = new Patient();
        Doctor doctor = new Doctor();
        doctor.setId(1);
        Appointment app1 = new Appointment();
        LocalDateTime today = LocalDateTime.now();
        // calcolo una data per il primo appuntamento
        LocalDateTime appointment1FutureDate = LocalDateTime.of(today.getYear()+1, today.getMonth(), today.getDayOfMonth(), today.getHour(), today.getMinute(), today.getSecond());
        app1.setAppointmentDate(appointment1FutureDate);
        app1.setText("Visita");
        app1.setPatient(patient);
        app1.setExamCategory(eC);
        app1.setDoctor(doctor);
        Appointment app2 = new Appointment();
        // calcolo una data per il primo appuntamento
        LocalDateTime appointment2FutureDate = LocalDateTime.of(today.getYear()+2, today.getMonth(), today.getDayOfMonth(), today.getHour(), today.getMinute(), today.getSecond());
        app2.setAppointmentDate(appointment2FutureDate);
        app2.setText("Visita Secondo appuntamento");
        app2.setPatient(patient);
        app2.setExamCategory(eC);
        app2.setDoctor(doctor);
        Appointment app3 = new Appointment();
        // calcolo una data per il primo appuntamento
        LocalDateTime appointment3FutureDate = LocalDateTime.of(today.getYear(), today.getMonth(), today.getDayOfMonth(), today.getHour()+1, today.getMinute(), today.getSecond());
        app3.setAppointmentDate(appointment3FutureDate);
        app3.setText("Visita Secondo appuntamento");
        app3.setPatient(patient);
        app3.setExamCategory(eC);
        app3.setDoctor(doctor);


        when(appointmentService.getAllAppointmentByDoctorId(doctor.getId())).thenReturn(Arrays.asList(app1, app2));
        when(appointmentService.getAllTodayAppointmentsByDoctorId(doctor.getId())).thenReturn(Arrays.asList(app3));

        this.mockMvc
                .perform(get("/nurse/agenda/DoctorAgenda/" + doctor.getId())
                        .principal(user))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("nurse/agenda/doctorAppointments.html"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("appointments"))
                .andExpect(MockMvcResultMatchers.model().attribute("appointments", Arrays.asList(app3)))
                .andDo(print());

    }
}
