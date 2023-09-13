package com.springboot.myhealthplatform.service;

import com.springboot.myhealthplatform.bean.Patient;
import com.springboot.myhealthplatform.board.bean.BloodTestReport;
import com.springboot.myhealthplatform.board.repository.BloodTestReportRepository;
import com.springboot.myhealthplatform.board.service.BloodTestReportService;
import jakarta.validation.ValidationException;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BloodTestReportServiceTest {

    @Mock
    private BloodTestReportRepository bloodTestReportRepository;
    @InjectMocks
    private BloodTestReportService bloodTestReportService;
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
     * Verifica che il report venga salvato correttamente
     */
    @Test
    public void it_should_save_the_blood_report() throws Exception {
        BloodTestReport bloodTestReport1 = new BloodTestReport();
        bloodTestReport1.setDescription("Esame del sangue");

        when(bloodTestReportRepository.save(ArgumentMatchers.any(BloodTestReport.class))).thenReturn(bloodTestReport1);

        BloodTestReport bloodTestReport2 = bloodTestReportService.save(bloodTestReport1);
        System.out.println("Nome esame che si sta salvando: " + bloodTestReport1.getDescription());
        System.out.println("Nome esame recuperato da db dopo il salvataggio: " + bloodTestReport2.getDescription());

        Assertions.assertThat(bloodTestReport2).usingRecursiveComparison().isEqualTo(bloodTestReport1);
    }

    /** -- SUPERATO --
     * Verifico che vengano recuperati correttamente gli esami del paziente
     * @throws Exception
     */
    @Test
    public void it_should_get_all_patient_s_blood_report() throws Exception {
        Patient patient = new Patient();
        patient.setId(1);
        BloodTestReport bloodTestReport1 = new BloodTestReport();
        bloodTestReport1.setDescription("Esame del sangue Uno");
        BloodTestReport bloodTestReport2 = new BloodTestReport();
        bloodTestReport2.setDescription("Esame del sangue Due");

        when(bloodTestReportRepository.findAllByPatientIdOrderByExaminationDateDesc(ArgumentMatchers.anyInt())).thenReturn(Arrays.asList(bloodTestReport1, bloodTestReport2));

        List<BloodTestReport> bloodTestReportList = bloodTestReportService.getAllBloodTestReports(patient.getId());
        System.out.println("Grandezza lista recuperata da db: " + bloodTestReportList.size());

        Assertions.assertThat(bloodTestReportList.get(0).getDescription()).isEqualTo(bloodTestReport1.getDescription());
        Assertions.assertThat(bloodTestReportList.get(1).getDescription()).isEqualTo(bloodTestReport2.getDescription());
    }

    /** -- SUPERATO --
     * Verificato che viene lanciata l'eccezione se per il paziente esiste già un report nella data
     * ricercata.
     * @throws Exception
     */

    @Test
    public void it_should_return_validation_exception_if_date_already_exist_in_db() throws Exception {
        Patient patient = new Patient();
        patient.setId(1);
        BloodTestReport bloodTestReport = new BloodTestReport();
        bloodTestReport.setPatient(patient);
        Date date = new Date();
        bloodTestReport.setExaminationDate(date);

        when(bloodTestReportRepository.findByExaminationDateAndPatientId(ArgumentMatchers.any(Date.class), ArgumentMatchers.anyInt())).thenReturn(bloodTestReport);

        assertThrows(ValidationException.class, () -> bloodTestReportService.findByDateForValidation(date, patient.getId()));

    }

    /** -- SUPERATO --
     * Verificato che viene lanciata l'eccezione se per il paziente esiste già un report con la stessa descrizione
     * ricercata.
     * @throws Exception
     */
    @Test
    public void it_should_return_validation_exception_if_description_already_exist_in_db() throws Exception {
        Patient patient = new Patient();
        patient.setId(1);
        BloodTestReport bloodTestReport = new BloodTestReport();
        bloodTestReport.setPatient(patient);
        bloodTestReport.setDescription("Esame");

        when(bloodTestReportRepository.findByDescriptionAndPatientId(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt())).thenReturn(bloodTestReport);

        assertThrows(ValidationException.class, () -> bloodTestReportService.findByDescriptionForValidation(bloodTestReport.getDescription(), patient.getId()));

    }
}
