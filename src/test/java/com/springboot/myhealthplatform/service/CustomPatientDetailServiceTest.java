package com.springboot.myhealthplatform.service;

import com.springboot.myhealthplatform.bean.*;
import com.springboot.myhealthplatform.login.CustomUserDetailsService;
import com.springboot.myhealthplatform.repository.DoctorRepository;
import com.springboot.myhealthplatform.repository.PatientRepository;
import com.springboot.myhealthplatform.repository.RoleRepository;
import com.springboot.myhealthplatform.repository.UserRepository;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CustomPatientDetailServiceTest {

    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private CustomUserDetailsService customUserDetailService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private PatientRepository patientRepository;
    @InjectMocks
    private CustomPatientDetailService customPatientDetailService;
    private AutoCloseable closeable;

    @BeforeEach
    void initService() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void closeService() throws Exception {
        closeable.close();
    }

    /** -- SUPERATO --
     * Verifica il salvataggio di un nuovo paziente e dello user ad esso collegato nel database.
     */
    @Test
    public void it_should_save_a_new_patient() {
        UserRegistrationDto userDto = new UserRegistrationDto();
        PatientRegistrationDto patientDto = new PatientRegistrationDto();
        User patientUser = new User();
        patientUser.setId(1L);
        Patient patient = new Patient();
        patient.setId(1);
        Role role = new Role();
        Doctor doctor = new Doctor();

        when(patientRepository.save(ArgumentMatchers.any(Patient.class))).thenReturn(patient);

        Patient patientSaved = customPatientDetailService.save(userDto, patientDto);

        assertThat(patient.getId() == patientSaved.getId());
    }

    /** -- SUPERATO --
     * Verifica che vengano recuperati tutti i pazienti presenti nel db.
     * @throws Exception
     */
    @Test
    public void it_should_return_all_patients() throws Exception {
        Patient patient1 = new Patient();
        patient1.setId(1);
        Patient patient2 = new Patient();
        patient2.setId(2);
        List<Patient> patientList = new ArrayList<>();

        patientList = customPatientDetailService.getAll();

        assertThat(patientList.size() == 2);

    }

    /** -- SUPERATO --
     * Verifica che, se non ci sono pazienti nel database, viene lanciata una eccezione (NullPointerException)
     * @throws Exception
     */
    @Test
    public void it_should_return_error_because_list_of_patient_is_null() throws Exception {
        Patient patient1 = new Patient();
        patient1.setId(1);
        Patient patient2 = new Patient();
        patient2.setId(2);
        List<Patient> patientList = new ArrayList<>();

        when(patientRepository.findAllByOrderBySurname()).thenReturn(null);

        assertThrows(NullPointerException.class, () -> customPatientDetailService.getAll());

    }

    /** -- SUPERATO --
     * Verifico che, dato lo username del paziente, il paziente venga recuperato dal database.
     * @throws Exception
     */
    @Test
    public void it_should_return_a_patient_by_username() throws Exception {
        User patientUser = new User();
        patientUser.setUsername("UsernamePatient");
        patientUser.setId(1L);
        Patient patient = new Patient();
        patient.setId(1);
        patient.setUser(patientUser);
        Role role = new Role();
        Doctor doctor = new Doctor();

        when(userRepository.findUserByUsername(ArgumentMatchers.anyString())).thenReturn(patientUser);
        when(patientRepository.findByUserId(ArgumentMatchers.anyLong())).thenReturn(patient);

        Patient patientSaved = customPatientDetailService.findPatientByUserUsername(patientUser.getUsername());

        assertThat(patientSaved.getUser().getUsername() == patient.getUser().getUsername());
    }

    /** -- SUPERATO --
     * Verifica che, dato l'id di un paziente, recuperi l'oggetto paziente corrispondente.
     * @throws Exception
     */
    @Test
    public void it_should_return_patient_by_Id() throws Exception {
        User patientUser = new User();
        patientUser.setUsername("UsernamePatient");
        patientUser.setId(1L);
        Patient patient = new Patient();
        patient.setName("NomePaziente");
        patient.setId(1);
        patient.setUser(patientUser);
        Role role = new Role();
        Doctor doctor = new Doctor();

        when(patientRepository.findById(ArgumentMatchers.anyInt())).thenReturn(patient);

        Patient patientSaved = customPatientDetailService.findPatientById(patient.getId());

        assertThat(patientSaved.getName() == patient.getName());
    }

    /** -- SUPERATO --
     * Verifica che, se non viene recuperato nessun paziente dal database, viene lanciata una eccezione.
     * @throws Exception
     */
    @Test
    public void it_should_return_an_exception_becase_patient_is_null() throws Exception {
        User patientUser = new User();
        patientUser.setUsername("UsernamePatient");
        patientUser.setId(1L);
        Patient patient = new Patient();
        patient.setName("NomePaziente");
        patient.setId(1);
        patient.setUser(patientUser);
        Role role = new Role();
        Doctor doctor = new Doctor();

        when(patientRepository.findById(ArgumentMatchers.anyInt())).thenReturn(null);

        assertThrows(Exception.class, () ->customPatientDetailService.findPatientById(patient.getId()));

    }

    /** -- SUPERATO --
     * Verifica che, se il codice fiscale esiste già nel database, allora viene lanciata una eccezione.
     * @throws Exception
     */
    @Test
    public void it_should_return_validation_error_because_CF_already_exist() throws Exception {
        Patient patient = new Patient();
        patient.setCF("CFValue");

        when(patientRepository.findByCF(ArgumentMatchers.anyString())).thenReturn(patient);
        assertThrows(Exception.class, () -> customPatientDetailService.findCFForValidation(patient.getCF()));
    }




}
