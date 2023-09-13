package com.springboot.myhealthplatform.board.repository;

import com.springboot.myhealthplatform.board.bean.BloodTestReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
/**
 * Metodi utili al recupero e salvataggio dei dati riferiti alla classe BloodTestReport nel database.
 */
public interface BloodTestReportRepository extends JpaRepository<BloodTestReport, Integer> {

    List<BloodTestReport> findAllByPatientId(int patientId);
    List<BloodTestReport> findAllByPatientIdOrderByExaminationDateDesc(int patientId);
    BloodTestReport findByExaminationDateAndPatientId(Date examinationDate, int patientId);
    BloodTestReport findByDescriptionAndPatientId(String description, int patientId);
}
