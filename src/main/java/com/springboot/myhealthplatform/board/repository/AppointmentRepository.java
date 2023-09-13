package com.springboot.myhealthplatform.board.repository;

import com.springboot.myhealthplatform.board.bean.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Metodi utili al recupero e salvataggio dei dati riferiti alla classe Appointment nel database.
 */
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

    /**
     * Ritorna la lista degli appuntamenti salvati per il paziente in ordine di data
     * @param patientId id del paziente di cui si vuole estrarre gli appuntamenti
     * @return la lista di oggetti di classe Appointment
     */
    List<Appointment> findAllByPatientIdOrderByAppointmentDateAsc(int patientId);


    List<Appointment> findAllByDoctorId(int doctorId);

    List<Appointment> findAllByDoctorIdAndAppointmentDate(int doctorId, Date appointmentDate);

    Appointment findByIdAndPatientId(int appointmentId, int patientId);
    /**
     * Ritorna la lista degli appuntamenti salvati per il medico in ordine di data
     * @param doctorId id del medico di cui si vogliono estrarre gli appuntamenti
     * @return la lista di oggetti di classe Appointment
     */
    List<Appointment> findAllByDoctorIdOrderByAppointmentDateAsc(int doctorId);

}

