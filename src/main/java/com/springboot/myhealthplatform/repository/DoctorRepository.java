package com.springboot.myhealthplatform.repository;

import com.springboot.myhealthplatform.bean.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 * Metodi utili al recupero e salvataggio dei dati riferiti alla classe Doctor nel database.
 */
@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Integer> {

    Doctor findById(int id);
    Doctor findDoctorByNameAndSurname(String userName, String userSurname);
    Doctor findByUserId(long userId);
    Doctor findByCF(String CF);
    Doctor findByBadgeNumber(String badgeNumber);

    List<Doctor> findAllByOrderBySurname();

}
//Spring automatically implements this repository interface in a bean that has the same name, but with a change in the case (patientRepository)