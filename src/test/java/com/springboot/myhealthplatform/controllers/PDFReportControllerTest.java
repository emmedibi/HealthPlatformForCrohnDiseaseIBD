package com.springboot.myhealthplatform.controllers;

import com.springboot.myhealthplatform.bean.Patient;
import com.springboot.myhealthplatform.board.bean.PDFReport;
import com.springboot.myhealthplatform.board.controller.PDFReportController;
import com.springboot.myhealthplatform.board.model.FileInfo;
import com.springboot.myhealthplatform.board.service.PDFReportService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PDFReportController.class)
@AutoConfigureMockMvc
public class PDFReportControllerTest {

    @MockBean
    private PDFReportService pdfReportService;
    @MockBean
    private CustomPatientDetailService customPatientDetailService;
    @MockBean
    private CustomDoctorDetailService customDoctorDetailService;
    @Autowired
    private MockMvc mockMvc;
    @Mock
    private Principal user;
    private String defaultUsername = "patient";

    /** -- SUPERATO --
     * Verifica che vengano restuiti correttamente i documenti PDF di uno specifico paziente.
     * @throws Exception
     */
    @Test
    @WithMockUser(username="patient",roles="PATIENT")
    public void it_should_return_patient_pdf_report() throws Exception {
        PDFReport pdf1 = new PDFReport();
        PDFReport pdf2 = new PDFReport();
        pdf1.setDescription("Report1");
        pdf2.setDescription("Report2");
        pdf1.setId(1);
        pdf2.setId(2);

        when(pdfReportService.getFile(pdf1.getId())).thenReturn(pdf1);

        mockMvc.perform(MockMvcRequestBuilders.get("/reports/files/" + pdf1.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


    /** -- SUPERATO --
     * Verifica che vengano recuperati tutti i documenti PDF per un paziente specifico. La richiesta viene
     * dall'utente con ruolo ROLE_DOCTOR
     * @throws Exception
     */
    @Test
    @WithMockUser(username="doctor",roles="DOCTOR")
    public void it_should_return_patient_pdf_report_from_doctor() throws Exception {
        PDFReport pdf1 = new PDFReport();
        PDFReport pdf2 = new PDFReport();
        pdf1.setDescription("Report1");
        pdf2.setDescription("Report2");
        pdf1.setId(1);
        pdf2.setId(2);

        when(pdfReportService.getFile(pdf1.getId())).thenReturn(pdf1);

        mockMvc.perform(MockMvcRequestBuilders.get("/doctor/reports/files/" + pdf1.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    /** -- NON SUPERATO : jakarta.servlet.ServletException: Circular view path [/reports/files]:
     * would dispatch back to the current handler URL [/reports/files] again.
     * Check your ViewResolver setup! (Hint: This may be the result of an unspecified view,
     * due to default view name generation.)
     * Per la risoluzione del problema, dovrei modificare l'annotazione del controller da @Controller
     * a @RestController.
     * Verifica l'estrazione da db di tutti i report di uno specifico paziente.
     * @throws Exception
     */
    @Test
    @WithMockUser(username="patient",roles="PATIENT")
    public void it_should_return_all_patient_reports() throws Exception {
        FileInfo fileInfo1 = new FileInfo("Report1", "url1");
        FileInfo fileInfo2 = new FileInfo("Report2", "url2");
        List<FileInfo> fileInfoList = new ArrayList<>();
        fileInfoList.add(fileInfo1);
        fileInfoList.add(fileInfo2);
        Patient patient = new Patient();
        patient.setId(3);
        PDFReport pdf1 = new PDFReport();
        PDFReport pdf2 = new PDFReport();
        pdf1.setDescription("Report1");
        pdf2.setDescription("Report2");
        pdf1.setId(1);
        pdf2.setId(2);

        when(customPatientDetailService.findPatientByUserUsername(defaultUsername)).thenReturn(patient);

        when(pdfReportService.getAllFiles(patient.getId())).thenReturn(Stream.of(pdf1, pdf2));

        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/reports/files"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("/reports/files"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("files"));

    }

}
