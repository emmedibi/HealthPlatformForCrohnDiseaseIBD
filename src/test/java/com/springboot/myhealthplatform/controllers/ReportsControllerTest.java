package com.springboot.myhealthplatform.controllers;

import com.springboot.myhealthplatform.board.bean.BloodTestReport;
import com.springboot.myhealthplatform.board.bean.PDFReport;
import com.springboot.myhealthplatform.board.controller.ReportsController;
import com.springboot.myhealthplatform.board.service.BloodTestReportService;
import com.springboot.myhealthplatform.board.service.PDFReportService;
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
import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ReportsController.class)
@AutoConfigureMockMvc
@WithMockUser(username="doctor",roles="DOCTOR")
public class ReportsControllerTest {

    @MockBean
    private BloodTestReportService bloodTestReportService;
    @MockBean
    private PDFReportService pdfReportService;
    @Autowired
    private MockMvc mockMvc;

    /** --SUPERATO --
     * Verifica della chiamata GET che recupera i documenti di un paziente in base al suo id
     * @throws Exception
     */
    @Test
    public void it_should_return_patient_s_files() throws Exception {
        BloodTestReport bloodTestReport1 = new BloodTestReport();
        BloodTestReport bloodTestReport2 = new BloodTestReport();
        PDFReport pdfReport1 = new PDFReport();
        PDFReport pdfReport2 = new PDFReport();
        int patientId = 1;


        when(bloodTestReportService.getAllBloodTestReports(patientId)).thenReturn(Arrays.asList(bloodTestReport1, bloodTestReport2));
        when(pdfReportService.getAllFiles(patientId)).then(i -> Stream.of(pdfReport1, pdfReport2));

        mockMvc.perform(MockMvcRequestBuilders.get("/doctor/reports/reportsPerPatient/{id}", 1))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("/doctor/reports/patientReportsBoard.html"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("bloodTestReports"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("files"));
    }
}
