package com.springboot.myhealthplatform.repository;

import com.springboot.myhealthplatform.bean.Patient;
import org.hibernate.query.sqm.SortOrder;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.naming.ldap.SortKey;
import javax.swing.*;
import java.util.List;
import java.util.Optional;
/**
 * Metodi utili al recupero e salvataggio dei dati riferiti alla classe Patient nel database.
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Integer> {

    Patient findPatientByNameAndSurname(String userName, String userSurname);
    Patient findById(int patientId);
    Patient findByUserId(Long userId);
    List<Patient> findByDoctorId(int doctorId);
    Optional<Patient> findByIdAndDoctorId(int patientId, int doctorId);
    Patient findByCF(String CF);

    List<Patient> findAllByOrderBySurname();

}
