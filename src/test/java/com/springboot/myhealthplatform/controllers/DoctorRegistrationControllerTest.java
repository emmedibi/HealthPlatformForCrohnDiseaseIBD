package com.springboot.myhealthplatform.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.myhealthplatform.bean.Doctor;
import com.springboot.myhealthplatform.bean.DoctorRegistrationDto;
import com.springboot.myhealthplatform.bean.User;
import com.springboot.myhealthplatform.bean.UserRegistrationDto;
import com.springboot.myhealthplatform.controller.DoctorRegistrationController;
import com.springboot.myhealthplatform.login.CustomUserDetailsService;
import com.springboot.myhealthplatform.service.CustomDoctorDetailService;
import jakarta.validation.ValidationException;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DoctorRegistrationController.class)
@AutoConfigureMockMvc
@WithMockUser(username="admin",roles="ADMIN")
public class DoctorRegistrationControllerTest {

    @MockBean
    private CustomDoctorDetailService customDoctorDetailService;
    @MockBean
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;
    @MockBean
    private ObjectMapper objectMapper;

    @Before()
    public void setup()
    {
        //Init MockMvc Object and build
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    /** -- SUPERATO --
     * Verifica che l'utenza venga salvata correttamente nel database.
     * @throws Exception
     */
    @Test
    public void it_should_save_doctor_in_database() throws Exception{
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
//        userRegistrationDto.setRole("ROLE_DOCTOR");
        DoctorRegistrationDto doctorRegistrationDto = new DoctorRegistrationDto();
        User user = new User();
        Doctor doctor = new Doctor("Giuseppe", "Rossi", "02550000", "RSSGPP80A01L219Q", "199901A", user);

        when(customDoctorDetailService.save(userRegistrationDto, doctorRegistrationDto)).thenReturn(doctor);

        mockMvc.perform(MockMvcRequestBuilders.post("/doctorRegistration")
                .content(asJsonString(doctor))
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("/admin/successRegistration.html"));

    }


//    @Test
//    public void it_should_return_error_page_because_CF_already_exists() throws Exception{
//        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
//        DoctorRegistrationDto doctorRegistrationDto = new DoctorRegistrationDto();
//        doctorRegistrationDto.setBadgeNumber("199901B");
//        doctorRegistrationDto.setCF("RSSGPP80A01L219Q");
//        String CF = "RSSGPP80A01L219Q";
//        User user = new User();
//        Doctor doctor = new Doctor("Giuseppe", "Rossi", "02550000", "RSSGPP80A01L219Q", "199901A", user);
//        Doctor doctor1 = new Doctor("Giuseppe Maria", "Rossi", "02551111", "RSSGPP80A01L219Q", "199901B", user);
//
//
//        doThrow(new ValidationException("CF is already used. Patient already presents in the DB."))
//                .when(customDoctorDetailService)
//                        .findCFForValidation(ArgumentMatchers.any(String.class));
//
//
//        mockMvc.perform(post("/doctorRegistration")
//                                .param("userRegistrationDto", "userRegistrationDto")
//                                .param("doctorRegistrationDto", "doctorRegistrationDto")
//                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
//                        .andDo(print())
//                        .andExpect(status().isOk())
//                        .andExpect(MockMvcResultMatchers.view().name("/admin/doctorRegistrationPage.html"))
//                        .andExpect(MockMvcResultMatchers.model().attributeExists("errorMessages"));
//
//    }

    /** -- SUPERATO --
     * Verifica che la registrazione dell'utente dottore vada a buon fine.
     * @throws Exception
     */
    @Test
    public void whenPostRequestToDoctorsAndValidDoctor_thenCorrectResponse() throws Exception {
            MediaType textPlainUtf8 = new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8);
            String user = "{\"name\": \"bob\", \"surname\": \"smith\", \"email\" : \"bob@domain.com\"}";
            String doctor = "{\"name\": \"bob\", \"surname\": \"smith\", \"email\" : \"bob@domain.com\", \"CF\" : \"RSSGPP80A01L219Q\"}";
            mockMvc.perform(MockMvcRequestBuilders.post("/doctorRegistration")
                            .content(user)
                            .content(doctor)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(MockMvcResultMatchers.status().isOk());

    }

    /** -- SUPERATO --
     * Verifica che la registrazione di un nuovo dottore dia esito negativo perché manca l'attributo
     * nome dell'oggetto User.
     * @throws Exception
     */
    @Test
    public void whenPostRequestToDoctorsAndInValidUser_thenCorrectResponse() throws Exception {
        String user = "{\"name\": \"\", \"surname\": \"smith\", \"email\" : \"bob@domain.com\"}";
        String doctor = "{\"name\": \"bob\", \"surname\": \"smith\", \"email\" : \"bob@domain.com\", \"CF\" : \"RSSGPP80A01L219Q\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/doctorRegistration")
                        .content(user)
                        .content(doctor)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("message"));
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
