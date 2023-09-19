package com.springboot.myhealthplatform.board.repository;

import com.springboot.myhealthplatform.board.bean.PDFReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.stream.Stream;
/**
 * Metodi utili al recupero e salvataggio dei dati riferiti alla classe PDFReport nel database.
 */
@Repository
public interface PDFReportRepository extends JpaRepository<PDFReport, Integer> {

    PDFReport findByDescription(String description);
    PDFReport findById(int id);

    Collection<PDFReport> findAllByPatientId(int patientId);

    PDFReport findByDescriptionAndPatientId(String description, int patientId);

}
