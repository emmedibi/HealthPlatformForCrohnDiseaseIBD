package com.springboot.myhealthplatform.controllers;

import com.springboot.myhealthplatform.bean.Doctor;
import com.springboot.myhealthplatform.bean.User;
import com.springboot.myhealthplatform.controller.AdminUserController;
import com.springboot.myhealthplatform.login.CustomUserDetailsService;
import com.springboot.myhealthplatform.service.CustomDoctorDetailService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AdminUserController.class)
@AutoConfigureMockMvc
@WithMockUser(username="admin",roles="ADMIN")
public class AdminControllerTest {

    @MockBean
    private CustomUserDetailsService customUserDetailsService;
    @MockBean
    private CustomDoctorDetailService customDoctorDetailService;
    @Autowired
    private MockMvc mockMvc;

    /** SUPERATO
     * Recupera tutti gli utenti del database.
     * @throws Exception
     */
    @Test
    public void it_should_return_all_the_users() throws Exception{
        User user1 = new User();
        User user2 = new User();
        user1.setName("userOneName");
        user2.setName("userTwoName");

        when(customUserDetailsService.getAll()).thenReturn(Arrays.asList(user1, user2));

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/showAllUsers"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("/admin/showAllUsers.html"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("users"));
    }

    /** SUPERATO
     * Recupera tutti i medici che dovranno essere mostrati a schermo durante la registrazione di un paziente.
     * @throws Exception
     */
    @Test
    public void it_should_return_Patient_Registration_Page_and_list_of_doctors() throws Exception {
        Doctor doctor1 = new Doctor();
        Doctor doctor2 = new Doctor();
        doctor1.setName("doctorOneName");
        doctor2.setName("doctorTwoName");

        when(customDoctorDetailService.getAll()).thenReturn(Arrays.asList(doctor1, doctor2));

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/patientRegistrationPage"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("/admin/patientRegistration.html"))
                .andExpect((MockMvcResultMatchers.model().attributeExists("doctors")));
    }

    /** SUPERATO
     * Recupera il messaggio presente sull'eccezione NullPointerException quando non ci sono medici registrati a sistema.
     * @throws Exception
     */
    @Test
    public void it_should_return_page_error_because_list_of_doctors_is_null() throws Exception{
        Doctor doctor = null;

        when(customDoctorDetailService.getAll()).thenThrow(new NullPointerException("Nessun medico registrato. Procedere a registrare un medico prima di registrare un paziente"));

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/patientRegistrationPage"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("/myErrorPage.html"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("message"));
    }
}
