package com.springboot.myhealthplatform.service;

import com.springboot.myhealthplatform.bean.Doctor;
import com.springboot.myhealthplatform.bean.Patient;
import com.springboot.myhealthplatform.board.bean.Appointment;
import com.springboot.myhealthplatform.board.bean.ExamCategory;
import com.springboot.myhealthplatform.board.repository.AppointmentRepository;
import com.springboot.myhealthplatform.board.repository.ExamCategoryRepository;
import com.springboot.myhealthplatform.board.service.AppointmentService;
import com.springboot.myhealthplatform.repository.PatientRepository;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private ExamCategoryRepository examCategoryRepository;
    private AutoCloseable closeable;
    @Mock
    private PatientRepository patientRepository;
    @InjectMocks
    private AppointmentService appointmentService;

    @BeforeEach
    void initService() {
        closeable = MockitoAnnotations.openMocks(this);
    }
    @AfterEach
    void closeService() throws Exception {
        closeable.close();
    }

    /** -- SUPERATO --
     * Test del salvataggio di un appuntamento su DB
     * @throws NullPointerException
     * @throws Exception
     */
    @Test
    public void when_save_appointment_it_should_return_appointment() throws NullPointerException, Exception{
        Appointment appointment = new Appointment();
        appointment.setText("VisitaTest");
        appointment.setPatient(new Patient());
        appointment.setDoctor(new Doctor());
        appointment.setAppointmentDate(LocalDateTime.now());
        ExamCategory examCategory = new ExamCategory();
        examCategory.setCategoryTitle("CategoryTest");
        String examTitle = "CategoryTest";

        when(examCategoryRepository.findByCategoryTitle(examTitle)).thenReturn(new ExamCategory("CategoryTest"));
        ExamCategory createdExamCategory = examCategoryRepository.findByCategoryTitle(examTitle);
        appointment.setExamCategory(createdExamCategory);

        when(appointmentRepository.save(appointment)).thenReturn(appointment);

        Appointment created = appointmentService.save(appointment);

        assertThat(appointment.getText()).isSameAs(created.getText());
    }

    /** -- SUPERATO --
     * Verifica lancio eccezione NullPointerException se non viene trovato nessun oggetto di classe
     * ExamCategory.
     * @throws NullPointerException
     * @throws Exception
     */
    @Test(expected = NullPointerException.class)
    public void when_save_appointment_with_null_examCategory() throws NullPointerException, Exception{
        Appointment appointment = new Appointment();
        appointment.setText(null); // testo di un carattere solo
        appointment.setPatient(new Patient());
        appointment.setDoctor(new Doctor());
        appointment.setAppointmentDate(LocalDateTime.now());
        ExamCategory examCategory = new ExamCategory();
        examCategory.setCategoryTitle("CategoryTest");
        String examTitle = "CategoryTest";
        String examCategoryTitle = null;

        ExamCategory createdExamCategory = examCategoryRepository.findByCategoryTitle(examCategoryTitle);
        appointment.setExamCategory(createdExamCategory);


        appointmentService.save(appointment);

    }

    /** -- SUPERATO --
     * Verifica il recupero degli appuntamenti con data successiva a oggi.
     * @throws Exception
     */
    @Test
    public void it_should_show_all_patient_appointments() throws Exception {
        // Creo due appuntamenti fittizzi //
        Appointment appointment1 = new Appointment();
        Appointment appointment2 = new Appointment();
        LocalDateTime today = LocalDateTime.now();
        // calcolo una data per il primo appuntamento
        LocalDateTime appointment1FutureDate = LocalDateTime.of(today.getYear()+1, today.getMonth(), today.getDayOfMonth(), today.getHour(), today.getMinute(), today.getSecond());
        appointment1.setAppointmentDate(appointment1FutureDate);
        // calcolo una data per il secondo appuntamento
        LocalDateTime appointment2FutureDate = LocalDateTime.of(today.getYear()+2, today.getMonth(), today.getDayOfMonth()+1, today.getHour(), today.getMinute(), today.getSecond());
        appointment2.setAppointmentDate(appointment2FutureDate);

        int patientId = 1;

        when(appointmentRepository.findAllByPatientIdOrderByAppointmentDateAsc(patientId)).thenReturn(Arrays.asList(appointment1, appointment2));
        List<Appointment> appointmentList = appointmentRepository.findAllByPatientIdOrderByAppointmentDateAsc(patientId);

        when(appointmentService.getAllAppointmentsByPatientId(patientId)).thenReturn(appointmentList);

        List<Appointment> createdAppointmentList = appointmentService.getAllAppointmentsByPatientId(patientId);
        // Valori recuperati
        System.out.println("Data primo appuntamento recuperato: " + createdAppointmentList.get(0).getAppointmentDate());
        System.out.println("Data secondo appuntamento recuperato: " + createdAppointmentList.get(1).getAppointmentDate());
        // Valori di riferimento
        System.out.println("Confronto primo appuntamento: " + appointment1.getAppointmentDate());
        System.out.println("Confronto secondo appuntamento: " + appointment2.getAppointmentDate());

        assertThat(createdAppointmentList.get(0).getAppointmentDate()).isSameAs(appointment1.getAppointmentDate());

    }

    /** -- SUPERATO --
     * Verifico che ritorni solo gli appuntamenti con data nel futuro
     * @throws Exception
     */
    @Test
    public void it_should_return_only_future_appointments() throws Exception {
        // Creo due appuntamenti fittizzi //
        Appointment appointment1 = new Appointment();
        Appointment appointment2 = new Appointment();
        LocalDateTime today = LocalDateTime.now();
        // calcolo una data per il primo appuntamento - APPUNTAMENTO NEL FUTURO -
        LocalDateTime appointment1FutureDate = LocalDateTime.of(today.getYear()+1, today.getMonth(), today.getDayOfMonth(), today.getHour(), today.getMinute(), today.getSecond());
        appointment1.setAppointmentDate(appointment1FutureDate);
        // calcolo una data per il secondo appuntamento - APPUNTAMENTO NEL PASSATO -
        LocalDateTime appointment2FutureDate = LocalDateTime.of(today.getYear()-2, today.getMonth(), today.getDayOfMonth()+1, today.getHour(), today.getMinute(), today.getSecond());
        appointment2.setAppointmentDate(appointment2FutureDate);

        int doctorId = 1;

        when(appointmentRepository.findAllByDoctorIdOrderByAppointmentDateAsc(doctorId)).thenReturn(Arrays.asList(appointment2, appointment1));
        List<Appointment> appointmentList = appointmentRepository.findAllByDoctorIdOrderByAppointmentDateAsc(doctorId);
        System.out.println("Quanti appuntamenti sono recuperati dal db: " + appointmentList.size());
        List<Appointment> futureAppointmentList = new ArrayList<>();
        for(Appointment a : appointmentList){
            if(((a.getAppointmentDate().getDayOfYear() >= today.now().getDayOfYear()) && a.getAppointmentDate().getYear()== today.now().getYear()) || a.getAppointmentDate().getYear() > today.now().getYear()){
                futureAppointmentList.add(a);
            }
        }
//        when(appointmentService.getAllAppointmentByDoctorId(doctorId)).thenReturn(futureAppointmentList);

        List<Appointment> createdAppointmentList = appointmentService.getAllAppointmentByDoctorId(doctorId);
        System.out.println("Quanti appuntamenti recuperati sono nel futuro: " + createdAppointmentList.size());
        System.out.println("Data dell'appuntamento recuperato: " + createdAppointmentList.get(0).getAppointmentDate());

        // Mi assicuro che l'appuntamento che viene recuperato sia quello nel futuro e che ci
        // sia solo quello.
        assertThat(createdAppointmentList.get(0).getAppointmentDate()).isSameAs(appointment1.getAppointmentDate());
        assertThat(createdAppointmentList.size() > 1).isFalse();

    }

    /** -- SUPERATO --
     * Verifica che l'appuntamento venga salvato correttamente.
     * @throws Exception
     */
    @Test
    public void it_should_return_an_appointment() throws Exception {
        int patientId = 2;
        int appointmentId = 1;
        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);

        when(appointmentRepository.findByIdAndPatientId(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(appointment);

        Appointment appointmentSaved = appointmentService.checkAppointmentUser(appointmentId, patientId);
        Assertions.assertThat(appointmentSaved).usingRecursiveComparison().isEqualTo(appointment);
    }

    /** -- SUPERATO --
     * Verifica che, nel caso in cui l'appuntamento non esistesse, venga lanciata una eccezione.
     * @throws Exception
     */
    @Test
    public void it_should_return_an_error_because_appointment_doesnt_exist() throws Exception {
        int patientId = 2;
        int appointmentId = 1;
        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        when(appointmentRepository.findByIdAndPatientId(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenThrow(NullPointerException.class);
        assertThrows(Exception.class, () -> appointmentService.checkAppointmentUser(appointmentId, patientId));

    }

    /** -- SUPERATO --
     * Verifico che venga recuperato l'appuntamento con data OGGI.
     * @throws Exception
     */
    @Test
    public void it_should_return_all_today_appointments() throws Exception {
        // Creo due appuntamenti fittizzi //
        Appointment appointment1 = new Appointment();
        Appointment appointment2 = new Appointment();
        LocalDateTime today = LocalDateTime.now();
        // calcolo una data per il primo appuntamento - APPUNTAMENTO NEL FUTURO -
        LocalDateTime appointment1TodayDate = LocalDateTime.of(today.getYear(), today.getMonth(), today.getDayOfMonth(), today.getHour()+1, today.getMinute(), today.getSecond());
        appointment1.setAppointmentDate(appointment1TodayDate);
        // calcolo una data per il secondo appuntamento - APPUNTAMENTO NEL FUTURO -
        LocalDateTime appointment2FutureDate = LocalDateTime.of(today.getYear(), today.getMonth(), today.getDayOfMonth()+1, today.getHour(), today.getMinute(), today.getSecond());
        appointment2.setAppointmentDate(appointment2FutureDate);

        int doctorId = 1;

        when(appointmentRepository.findAllByDoctorIdOrderByAppointmentDateAsc(doctorId)).thenReturn(Arrays.asList(appointment2, appointment1));
        List<Appointment> appointmentList = appointmentRepository.findAllByDoctorIdOrderByAppointmentDateAsc(doctorId);
        System.out.println("Quanti appuntamenti sono recuperati dal db: " + appointmentList.size());
        List<Appointment> todayAppointment = new ArrayList<>();
        todayAppointment.add(appointment1);
//        when(appointmentService.getAllAppointmentByDoctorId(doctorId)).thenReturn(todayAppointment);

        List<Appointment> createdAppointmentList = appointmentService.getAllTodayAppointmentsByDoctorId(doctorId);
        System.out.println("Quanti appuntamenti recuperati sono di oggi: " + createdAppointmentList.size());
        System.out.println("Data dell'appuntamento recuperato: " + createdAppointmentList.get(0).getAppointmentDate());
        // Mi assicuro che l'appuntamento che viene recuperato sia quello di oggi e che ci
        // sia solo quello.
        assertThat(createdAppointmentList.get(0).getAppointmentDate()).isSameAs(appointment1.getAppointmentDate());
        assertThat(createdAppointmentList.size() > 1).isFalse();

    }

}
