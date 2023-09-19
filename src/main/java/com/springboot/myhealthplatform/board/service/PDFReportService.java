package com.springboot.myhealthplatform.board.service;

import com.springboot.myhealthplatform.bean.Doctor;
import com.springboot.myhealthplatform.bean.Patient;
import com.springboot.myhealthplatform.board.bean.PDFReport;
import com.springboot.myhealthplatform.board.repository.PDFReportRepository;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Stream;
/**
 * Classe che comunica con le classi Repository per estrarre o registrare dati provenienti dai Controller.
 * Service dedicato alla gestione dei documenti (PDFReport class).
 */
@Service
public class PDFReportService {

    @Autowired
    private PDFReportRepository pdfReportRepository;

    /**
     * Riceve il file caricato dall'utente e il paziente loggato a sistema, estrae la
     * descrizione del file e carica nel database un istanza di PDFReport.
     * @param file documento caricato a sistema
     * @param patient paziente loggato a sistema.
     * @return un oggetto di classe PDFReport salvato a sistema.
     * @throws IOException se il salvataggio non va a buon fine
     */
    public PDFReport store(MultipartFile file, Patient patient) throws IOException {
        // recupero il nome del file caricato.
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        try{
            pdfReportRepository.findByDescriptionAndPatientId(fileName, patient.getId());
        } catch(ValidationException e){
            throw e;
        }
        PDFReport fileDB = new PDFReport(fileName, file.getContentType(), file.getBytes(), patient);
        return pdfReportRepository.save(fileDB);
    }

    /**
     * Riceve il file caricato dall'utente, il medico loggato il paziente a cui
     * è riferito il documento. Estrae la descrizione del file e carica nel database un
     * istanza di PDFReport.
     * @param file documento caricato a sistema
     * @param patient paziente a cui è riferito il documento.
     * @param doctor medico loggao a sistema.
     * @return un oggetto di classe PDFReport salvato a sistema.
     * @throws IOException se il salvataggio non va a buon fine
     */
    public PDFReport storeByDoctor(MultipartFile file, Patient patient, Doctor doctor) throws IOException, ValidationException {
        // recupero il nome del file caricato.
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        try{
            fileNameCheck(fileName, patient);
        } catch(ValidationException e){
            throw e;
        }
        PDFReport fileDB = new PDFReport(fileName, file.getContentType(), file.getBytes(), patient, doctor);
        return pdfReportRepository.save(fileDB);
    }

    public void fileNameCheck(String fileName, Patient patient) throws ValidationException{
        if(pdfReportRepository.findByDescriptionAndPatientId(fileName, patient.getId()) != null){
            System.out.println("The pdf already exist for this patient");
            throw new ValidationException("The pdf already exist for this patient");
        } else {
            System.out.println("NOPE");
        }
    }

    /**
     * Recupera un file in base al suo identificativo
     * @param id identificativo del file
     * @return un'istanza PDFReport che contiene il documento.
     */
    public PDFReport getFile(int id) {
        return pdfReportRepository.findById(id);
    }

    /**
     * Recupera i file in base all'identificativo del paziente
     * @param patientId identificativo del paziente
     * @return uno Stream di oggetti PDFReport.
     */
    public Stream<PDFReport> getAllFiles(int patientId) {return pdfReportRepository.findAllByPatientId(patientId).stream();}

}
