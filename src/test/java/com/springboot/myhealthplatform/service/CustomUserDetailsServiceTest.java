package com.springboot.myhealthplatform.service;

import com.springboot.myhealthplatform.bean.Role;
import com.springboot.myhealthplatform.bean.User;
import com.springboot.myhealthplatform.login.CustomUserDetailsService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;
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
     * Verifica che dato uno username, venga restiuito lo User di tipo ROLE_NURSE presente nel database.
     * @throws Exception
     */
    @Test
    public void it_should_return_nurse() throws Exception {
        User nurse = new User();
        nurse.setUsername("InfUsername");
        Role role = new Role("ROLE_NURSE");
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        nurse.setRoles(roles);

        when(userRepository.findUserByUsername(nurse.getUsername())).thenReturn(nurse);

        User nurseSaved = customUserDetailsService.findNurseByUsername(nurse.getUsername());

        assertThat(nurseSaved.getUsername() == nurse.getUsername());

    }

    /** -- SUPERATO --
     * Verifica che venga lanciata una eccezione se l'utente Nurse che viene recuperato in base alla username,
     * non abbia il ruolo ROLE_NURSE richiesto.
     * @throws Exception
     */
    @Test
    public void it_should_return_an_exception_because_nurse_doesnt_exist() throws Exception {
        User nurse = new User();
        nurse.setUsername("InfUsername");
        Role role = new Role("ROLE_OTHER");
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        nurse.setRoles(roles);

        when(userRepository.findUserByUsername(nurse.getUsername())).thenReturn(nurse);

        assertThrows(Exception.class, () -> customUserDetailsService.findNurseByUsername(nurse.getUsername()));

    }

    /** -- SUPERATO --
     * Verifica che dato uno username, venga restiuito lo User di tipo ROLE_DOCTOR presente nel database.
     * @throws Exception
     */
    @Test
    public void it_should_return_doctor_user() throws Exception {
        User doctorUser = new User();
        doctorUser.setUsername("doctorUsername");
        Role role = new Role("ROLE_DOCTOR");
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        doctorUser.setRoles(roles);

        when(userRepository.findUserByUsername(doctorUser.getUsername())).thenReturn(doctorUser);

        User doctorSaved = customUserDetailsService.findDoctorByUsername(doctorUser.getUsername());

        assertThat(doctorSaved.getUsername() == doctorUser.getUsername());
    }

    /** -- SUPERATO --
     * Verifica che venga lanciata una eccezione se l'utente Doctor che viene recuperato in base alla username,
     * non abbia il ruolo ROLE_DOCTOR richiesto.
     * @throws Exception
     */
    @Test
    public void it_should_return_an_exception_because_doctor_user_is_null() throws Exception {
        User doctorUser = new User();
        doctorUser.setUsername("doctorUsername");
        Role role = new Role("ROLE_OTHER");
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        doctorUser.setRoles(roles);

        when(userRepository.findUserByUsername(doctorUser.getUsername())).thenReturn(doctorUser);

        assertThrows(Exception.class, () -> customUserDetailsService.findDoctorByUsername(doctorUser.getUsername()));

    }

    /** -- SUPERATO --
     * Verifica che se lo username è già presente nel database, viene lanciata una eccezione.
     * @throws Exception
     */
    @Test
    public void it_should_return_validation_exception_because_username_is_already_used() throws Exception {
        User user = new User("Username", "Password");
        when(userRepository.findUserByUsername(ArgumentMatchers.anyString())).thenReturn(user);

        assertThrows(Exception.class, () -> customUserDetailsService.findByUsernameForValidation(user.getUsername()));
    }

    /** -- SUPERATO --
     * Verifica che se la mail è già presente nel database, viene lanciata una eccezione.
     * @throws Exception
     */
    @Test
    public void it_should_return_validation_exception_because_email_is_already_used() throws Exception {
        User user = new User("Username", "Password");
        user.setEmail("email");
        when(userRepository.findByEmail(ArgumentMatchers.anyString())).thenReturn(user);

        assertThrows(Exception.class, () -> customUserDetailsService.findEmailForValidation(user.getEmail()));
    }

    /** -- SUPERATO --
     * Verifica che se il nome e il cognome in input è già presente nel database, viene lanciata una eccezione.
     * @throws Exception
     */
    @Test
    public void it_should_return_validation_exception_because_nameAndSurname_is_already_used() throws Exception {
        User user = new User("Username", "Password");
        user.setName("Name");
        user.setSurname("Surname");
        when(userRepository.findByNameAndSurname(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(user);

        assertThrows(Exception.class, () -> customUserDetailsService.findByNameAndSurnameForValidation(user.getName(), user.getSurname()));
    }


}
