package com.springboot.myhealthplatform.board.repository;

import com.springboot.myhealthplatform.board.bean.CrohnDiseaseActivityData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
/**
 * Metodi utili al recupero e salvataggio dei dati riferiti alla classe CrohnDiseaseActivityData nel database.
 */
@Repository
public interface CrohnDiseaseActivityDataRepository extends JpaRepository<CrohnDiseaseActivityData, Integer> {
}
