package com.springboot.myhealthplatform.board.service;

import com.springboot.myhealthplatform.board.bean.BloodTestReport;
import com.springboot.myhealthplatform.board.repository.BloodTestReportRepository;
import com.springboot.myhealthplatform.board.repository.PDFReportRepository;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
/**
 * Classe che comunica con le classi Repository per estrarre o registrare dati provenienti dai Controller.
 * Service dedicato alla gestione degli esami del sangue (BloodTestReport class).
 */
@Service
public class BloodTestReportService {

    @Autowired
    private BloodTestReportRepository bloodTestReportRepository; // bloodTestfReportRepository

    /**
     * Salvataggio su DB dei dati derivanti dalla form.
     * @param bloodTestReport oggetto che contiene tutti i dati derivanti dalla form e precedentmente validati.
     * @return l'oggetto di classe BloodTestReport appena salvato.
     */
    public BloodTestReport save(BloodTestReport bloodTestReport){
        return bloodTestReportRepository.save(bloodTestReport);
    }

    /**
     * Recupera tutti gli esami del sangue appartenenti ad un determinato paziente.
     * @param patientId identificativo di un paziente.
     * @return la lista di tutti gli oggetti di classe BloodTestReport per il paziente specifico.
     */
    public List<BloodTestReport> getAllBloodTestReports(int patientId) {
        return bloodTestReportRepository.findAllByPatientIdOrderByExaminationDateDesc(patientId);
    }

    /**
     * Verifica che per la data indicata e il paziente indicato non sia già presente a sistema un referto
     * degli esami del sangue.
     * @param examinationDate data recuperata dalla form.
     * @param patientId identificativo del paziente.
     * @throws ValidationException eccezione sollevata se nel database si trova un record con stessa data
     * e dello stesso paziente di quelli riportati in input.
     */
    public void findByDateForValidation(Date examinationDate, int patientId) throws ValidationException{
        if(bloodTestReportRepository.findByExaminationDateAndPatientId(examinationDate, patientId) != null){
            throw new ValidationException("This test report is already saved");
        }
    }

    /**
     * Verifica che per il paziente non sia già registrato a sistema un referto degli esami del sangue con
     * loa stessa descrizione.
     * @param description descrizione riportata nella form.
     * @param patientId identificativo del paziente.
     * @throws ValidationException viene sollevata questa eccezione se nel database esiste già un record del
     * paziente con stessa descrizione.
     */
    public void findByDescriptionForValidation(String description, int patientId) throws ValidationException{
        if(bloodTestReportRepository.findByDescriptionAndPatientId(description, patientId) != null){
            throw new ValidationException("This description is already used for another report");
        }
    }

}
