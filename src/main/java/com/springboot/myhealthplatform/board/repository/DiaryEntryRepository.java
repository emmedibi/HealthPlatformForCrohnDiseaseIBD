package com.springboot.myhealthplatform.board.repository;

import com.springboot.myhealthplatform.bean.Patient;
import com.springboot.myhealthplatform.board.bean.DiaryEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
/**
 * Metodi utili al recupero e salvataggio dei dati riferiti alla classe DiaryEntry nel database.
 */
@Repository
public interface DiaryEntryRepository extends JpaRepository<DiaryEntry, Integer> {
    List<DiaryEntry> findAllByPatientId(int patientId);

    /**
     * Recupera tutte le pagine di diario di un determinato paziente e di un determinato stato (Draft o
     * Published) e le ordina in base alla data
     * @param patientId identificativo del paziente
     * @param entryState stato della pagina di diario
     * @return lista di oggetti DiaryEntry ordinati per data discendente.
     */
    List<DiaryEntry> findAllByPatientIdAndEntryStateOrderByDateDesc(int patientId, String entryState);
    DiaryEntry findById(int diaryId);
    DiaryEntry findByIdAndPatientId(int id, int patientId);
    Patient findPatientById(int diaryEntryId);
    DiaryEntry findByDateAndPatientId(Date diaryEntryDate, int patientId);

}
