package com.springboot.myhealthplatform.service;

import com.springboot.myhealthplatform.bean.Doctor;
import com.springboot.myhealthplatform.bean.Patient;
import com.springboot.myhealthplatform.board.bean.PDFReport;
import com.springboot.myhealthplatform.board.repository.PDFReportRepository;
import com.springboot.myhealthplatform.board.service.PDFReportService;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PDFReportServiceTest {

    @Mock
    private PDFReportRepository pdfReportRepository;

    @InjectMocks
    private PDFReportService pdfReportService;

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
     * Verifica che il file PDF (che è di classe MultipartFile) venga salvato correttamente
     * nel database come oggetto PDFReport.
     * @throws Exception
     */
    @Test
    public void it_should_store_multipart_file() throws Exception {
        PDFReport pdfReport = new PDFReport();
        byte[] content = new byte[100];
        MultipartFile file = new MockMultipartFile("NomeFile", content);
        pdfReport.setDescription("Descrizione PDF");
        pdfReport.setId(1);
        pdfReport.setData(content);
        pdfReport.setType(file.getContentType());
        pdfReport.setVisitDate(new Date());
        Patient patient = new Patient();
        pdfReport.setPatient(patient);

        when(pdfReportRepository.save(ArgumentMatchers.any(PDFReport.class))).thenReturn(pdfReport);

        PDFReport pdfReportSaved = pdfReportService.store(file, patient);

        assertTrue(pdfReportSaved.getId()== pdfReport.getId());
    }

    /** -- SUPERATO --
     * Verifica che un file PDF (che è di classe MultipartFile) caricato dal medico venga correttamente
     * salvata con classe PDFReport.
     * @throws Exception
     */
    @Test
    public void it_should_store_multipart_file_from_Doctor() throws Exception {
        PDFReport pdfReport = new PDFReport();
        byte[] content = new byte[100];
        MultipartFile file = new MockMultipartFile("NomeFile", content);
        pdfReport.setDescription("Descrizione PDF");
        pdfReport.setId(1);
        pdfReport.setData(content);
        pdfReport.setType(file.getContentType());
        pdfReport.setVisitDate(new Date());
        Patient patient = new Patient();
        Doctor doctor = new Doctor();
        pdfReport.setPatient(patient);
        pdfReport.setDoctor(doctor);

        when(pdfReportRepository.save(ArgumentMatchers.any(PDFReport.class))).thenReturn(pdfReport);

        PDFReport pdfReportSaved = pdfReportService.storeByDoctor(file, patient, doctor);

        assertTrue(pdfReportSaved.getId()== pdfReport.getId());
    }


}
