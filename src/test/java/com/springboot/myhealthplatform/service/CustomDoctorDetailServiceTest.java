package com.springboot.myhealthplatform.service;

import com.springboot.myhealthplatform.bean.Doctor;
import com.springboot.myhealthplatform.bean.User;
import com.springboot.myhealthplatform.controller.AdminUserController;
import com.springboot.myhealthplatform.login.CustomUserDetailsService;
import com.springboot.myhealthplatform.repository.DoctorRepository;
import com.springboot.myhealthplatform.repository.RoleRepository;
import com.springboot.myhealthplatform.repository.UserRepository;
import com.springboot.myhealthplatform.service.CustomDoctorDetailService;
import jakarta.validation.ValidationException;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.Principal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CustomDoctorDetailService.class)
@AutoConfigureMockMvc
@WithMockUser(username="admin",roles="ADMIN")
public class CustomDoctorDetailServiceTest {

    @MockBean
    private CustomUserDetailsService customUserDetailsService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private DoctorRepository doctorRepository;
    @MockBean
    private RoleRepository roleRepository;
    @MockBean
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private CustomDoctorDetailService customDoctorDetailService;

    @BeforeEach
    void init(){
        customDoctorDetailService = new CustomDoctorDetailService(customUserDetailsService, userRepository, roleRepository, doctorRepository);
    }

    /** -- SUPERATO --
     * Verifica che venga recuperato un utente Doctor in base al suo id
     * @throws Exception
     */
    @Test
    public void find_a_doctor_by_id() throws Exception {

        User user = new User("doctor", "doctorPassword");
        user.setId(1L);
        Doctor doctor = new Doctor("nameDoctor", "surnameDoctor", "phone", "CF", "badgeNumber", user);
        when(doctorRepository.save(ArgumentMatchers.any(Doctor.class))).thenReturn(doctor);
        when(userRepository.findUserByUsername(ArgumentMatchers.any(String.class))).thenReturn(user);
        when(doctorRepository.findByUserId((ArgumentMatchers.any(Long.class)))).thenReturn(doctor);
/*        when(userRepository.save(ArgumentMatchers.any(User.class))).thenAnswer(i -> i.getArguments()[0]);
        when(doctorRepository.save(ArgumentMatchers.any(Doctor.class))).thenAnswer(i -> i.getArguments()[0]);*/
        User savedUser = userRepository.findUserByUsername("doctor");
       Doctor doctorInDB = customDoctorDetailService.findDoctorByUsername(user.getUsername());

       assertThat(doctorInDB).usingRecursiveComparison().isEqualTo(doctor);

    }

    /** -- SUPERATO --
     * Verifica che venga lanciata correttamente l'eccezione per cui, dato uno user, l'utente a lui
     * legato non è un utente Doctor
     * @throws Exception
     */
    @Test
    public void it_should_return_an_exception_when_doctor_is_null() throws Exception {

        User user = new User("doctor", "doctorPassword");
        user.setId(2L);
        User userDoctor = new User("doctor", "doctorPassword");
        user.setId(1L);
        Doctor doctor = new Doctor("nameDoctor", "surnameDoctor", "phone", "CF", "badgeNumber", userDoctor);
        when(doctorRepository.save(ArgumentMatchers.any(Doctor.class))).thenReturn(doctor);
        when(userRepository.findUserByUsername(ArgumentMatchers.any(String.class))).thenReturn(user);
        when(doctorRepository.findByUserId((ArgumentMatchers.any(Long.class)))).thenThrow(new NullPointerException("Nessun utente con ruolo medico trovato con questo username"));

        Exception exception = assertThrows(Exception.class, () -> customDoctorDetailService.findDoctorByUsername(user.getUsername()));
        String expectedMessage = "Nessun utente con ruolo medico trovato con questo username";
        String actualMessage = exception.getMessage();
        assertThat(actualMessage.contains(expectedMessage));

    }

    /** -- SUPERATO --
     * Verifica che venga lanciata correttamente l'eccezione sulla validazione del codice fiscale, che non deve
     * già esistere per un altro paziente.
     * @throws Exception
     */
    @Test
    public void it_should_throw_a_validation_exception_for_CF() throws Exception {
        User user = new User("doctor", "doctorPassword");
        user.setId(2L);
        User userDoctor = new User("doctor", "doctorPassword");
        user.setId(1L);
        Doctor doctorSaved = new Doctor("nameDoctor", "surnameDoctor", "phone", "CF", "badgeNumber", user);
        Doctor doctorTest = new Doctor("nameDoctor", "surnameDoctor", "phone", "CF", "badgeNumber", userDoctor);
        when(doctorRepository.save(ArgumentMatchers.any(Doctor.class))).thenReturn(doctorSaved);
        when(doctorRepository.findByCF(doctorTest.getCF())).thenThrow(ValidationException.class);

        assertThrows(Exception.class, () -> customDoctorDetailService.findCFForValidation(doctorTest.getCF()));
    }

    /** -- SUPERATO --
     * Verifica che venga lanciata correttamente l'eccezione sulla validazione del codice fiscale, che non deve
     * già esistere per un altro paziente.
     * @throws Exception
     */
    @Test
    public void it_should_throw_a_validation_exception_for_badge_number() throws Exception {
        User user = new User("doctor", "doctorPassword");
        user.setId(2L);
        User userDoctor = new User("doctor", "doctorPassword");
        user.setId(1L);
        Doctor doctorSaved = new Doctor("nameDoctor", "surnameDoctor", "phone", "CF", "badgeNumber", user);
        Doctor doctorTest = new Doctor("nameDoctor", "surnameDoctor", "phone", "CF", "badgeNumber", userDoctor);
        when(doctorRepository.save(ArgumentMatchers.any(Doctor.class))).thenReturn(doctorSaved);
        when(doctorRepository.findByBadgeNumber(doctorTest.getBadgeNumber())).thenThrow(ValidationException.class);

        assertThrows(ValidationException.class, () -> customDoctorDetailService.findBadgeNumberForValidation(doctorTest.getBadgeNumber()));

    }


}
