package com.springboot.myhealthplatform.board.repository;

import com.springboot.myhealthplatform.board.bean.ExamCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
/**
 * Metodi utili al recupero e salvataggio dei dati riferiti alla classe ExamCategory nel database.
 */
@Repository
public interface ExamCategoryRepository extends JpaRepository<ExamCategory, Integer> {

    ExamCategory findByCategoryTitle(String categoryTitle);

    ExamCategory findById(int examCategoryId);
}
