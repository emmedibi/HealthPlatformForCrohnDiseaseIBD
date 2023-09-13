package com.springboot.myhealthplatform.service;

import com.springboot.myhealthplatform.board.bean.ExamCategory;
import com.springboot.myhealthplatform.board.repository.ExamCategoryRepository;
import com.springboot.myhealthplatform.board.service.ExamCategoryService;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExamCategoryServiceTest {

    @Mock
    private ExamCategoryRepository examCategoryRepository;

    @InjectMocks
    private ExamCategoryService examCategoryService;

    private AutoCloseable closeable;

    @BeforeEach
    void initService() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void closeService() throws Exception {
        closeable.close();
    }

    /** -- SUPERATO --
     * Verifica che una categoria esame venga salvata correttamente
     * @throws Exception
     */
    @Test
    public void it_should_save_a_new_exam_category() throws Exception {
        ExamCategory newExamCategory = new ExamCategory("Nuova categoria");

        when(examCategoryRepository.save(ArgumentMatchers.any(ExamCategory.class))).thenReturn(newExamCategory);
        ExamCategory examCategorySaved = examCategoryService.save(newExamCategory);

        assertTrue(examCategorySaved.getCategoryTitle().equals(newExamCategory.getCategoryTitle()));

    }

    /** -- SUPERATO --
     * Verifica che vengano recuperate tutte le categorie di esami già salvate nel database
     * @throws Exception
     */
    @Test
    public void it_should_find_all_exam_categories() throws Exception {
        ExamCategory examCategory1 = new ExamCategory("Categoria Uno");
        ExamCategory examCategory2 = new ExamCategory("Categoria Due");

        List<ExamCategory> examCategoryList = new ArrayList<>();

        when(examCategoryRepository.findAll()).thenReturn(Arrays.asList(examCategory1, examCategory2));

        examCategoryList = examCategoryService.findAll();

        assertTrue(examCategoryList.size() == 2);
    }

    /** -- SUPERATO --
     * Verifica che, non trovando nessuna categoria di esami nel database, venga lanciata una eccezione.
     * @throws Exception
     */
    @Test
    public void it_should_return_exception_finding_all_exam_categories() throws Exception {
        ExamCategory examCategory1 = new ExamCategory("Categoria Uno");
        ExamCategory examCategory2 = new ExamCategory("Categoria Due");

        List<ExamCategory> examCategoryList = new ArrayList<>();

        when(examCategoryRepository.findAll()).thenReturn(null);

        assertThrows(Exception.class, () -> examCategoryService.findAll());
    }
}
