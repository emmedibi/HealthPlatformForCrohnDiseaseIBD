package com.springboot.myhealthplatform.board.service;

import com.springboot.myhealthplatform.board.bean.ExamCategory;
import com.springboot.myhealthplatform.board.repository.ExamCategoryRepository;
import jakarta.validation.constraints.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
/**
 * Classe che comunica con le classi Repository per estrarre o registrare dati provenienti dai Controller.
 * Service dedicato alla gestione delle categorie di esame (ExamCategory class).
 */
@Service
public class ExamCategoryService {

    @Autowired
    private ExamCategoryRepository examCategoryRepository;

    /**
     * Salvataggio delle nuova categoria di esame, proveniente dalla form compilata dall'utente
     * admin.
     * @param examCategory oggetto che contiene i dati da salvare
     * @return l'oggetto di classe ExamCategory appena salvato
     * @throws Exception viene lanciata se esiste già una categoria esame con le stesse
     * caratteristiche di quella che stiamo salvando.
     */
    public ExamCategory save(ExamCategory examCategory) throws Exception{
        if(!checkExamCategory(examCategory)){
            throw new Exception("Categoria gia' esistente");
        }
        return examCategoryRepository.save(examCategory);
    }

    /** -- NOT USED --
     * Ricerca una categoria esame tramite il suo titolo.
     * @param examCategoryTitle Stringa che corrisponde all'attributo title della classe
     *                          ExamCategory
     * @return
     */
    public ExamCategory findByCategoryTitle(String examCategoryTitle){
        return examCategoryRepository.findByCategoryTitle(examCategoryTitle);
    }

    /**
     * Cerca un record tramite il suo id.
     * @param examCategoryId identificatore dell'oggetto di classe ExamCategory
     * @return l'oggetto di classe ExamCategory che corrisponde ai criteri di ricerca.
     */
    public ExamCategory findById(int examCategoryId){
        return examCategoryRepository.findById(examCategoryId);
    }

    /**
     * Recupero di tutte le categorie di esami configurate a sistema.
     * @return la lista di categorie di esami configurate.
     * @throws NullPointerException se non esiste nessuna categoria di esame configurata a sistema.
     */
    public List<ExamCategory> findAll() throws NullPointerException{
        List<ExamCategory> examCategories = examCategoryRepository.findAll();
        if (examCategories == null){
            throw new NullPointerException("Nessuna categoria di esame configurata. Richiedere all'administrator di procedere alla creazione di almeno una categoria prima di creare un appuntamento");
        }
        return examCategories;
    }

    /**
     * Verifica se esiste già un record riferito alla classe ExamCategory con uno specifico Title.
     * @param examCategory oggetto di classe ExamCategory usato per verificare se un oggetto di
     *                     uguali attributi sia già presente nel database.
     * @return ritorna true se già esiste un record che combacia con i criteri di ricerca indicati, false
     * altrimenti.
     */
    public boolean checkExamCategory(ExamCategory examCategory){
        if(examCategoryRepository.findByCategoryTitle(examCategory.getCategoryTitle()) == null){
            return true;
        }
        return false;
    }
}
