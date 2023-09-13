package com.springboot.myhealthplatform.board.service;

import com.springboot.myhealthplatform.bean.Patient;
import com.springboot.myhealthplatform.board.bean.DiaryEntry;
import com.springboot.myhealthplatform.board.designPattern.DraftState;
import com.springboot.myhealthplatform.board.designPattern.SaveState;
import com.springboot.myhealthplatform.board.repository.DiaryEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
/**
 * Classe che comunica con le classi Repository per estrarre o registrare dati provenienti dai Controller.
 * Service dedicato alla gestione delle pagine di diario (DiaryEntry class).
 */
@Service
public class DiaryEntryService {
    @Autowired
    private DiaryEntryRepository diaryEntryRepository;

    public DiaryEntry save(DiaryEntry diaryEntry){
        diaryEntry.setEntryState("Draft");
        return diaryEntryRepository.save(diaryEntry);
    }

    /**
     * Recupera tutte le note pubblicate per uno specifico paziente.
     * @param patientId identificativo del paziente
     * @return lista di note del diario pubblicate.
     */
    public List<DiaryEntry> getAllPublishedDiaryEntries(int patientId){
        return diaryEntryRepository.findAllByPatientIdAndEntryStateOrderByDateDesc(patientId, "Published");
    }

    /**
     * Recupera tutte le note in bozza per uno specifico paziente.
     * @param patientId identificativo del paziente
     * @return lista di note del diario ancora in bozza.
     */
    public List<DiaryEntry> getAllDraftDiaryEntries(int patientId){
        return diaryEntryRepository.findAllByPatientIdAndEntryStateOrderByDateDesc(patientId, "Draft");
    }

    /**
     * Recupera una specifica pagina di diario e ne definisce lo stato.
     * @param id identificativo della pagina di diario.
     * @return la pagina di diario specifica richiesta.
     * @throws NullPointerException viene lanciata se non viene trovata la pagina di diario richiesta.
     */
    public DiaryEntry getDiaryEntryDraftById(int id) throws NullPointerException{
        DiaryEntry diaryEntry;
        try {
            diaryEntry = diaryEntryRepository.findById(id);
            if(diaryEntry != null){
                // Se ha recuperato almeno una pagina di diario, inizializzare
                // l'attributo State in base all'attributo EntryState
                // Se è una bozza, cioè entryState == "Draft" allora
                // inizializzo settando un oggetto DraftState
                if(diaryEntry.getEntryState().equals("Draft")){
                    diaryEntry.setState(new DraftState(diaryEntry));
                    // Se è una bozza, cioè entryState == "Published" allora
                    // inizializzo settando un oggetto SaveState
                } else if(diaryEntry.getEntryState().equals("Published")){
                    diaryEntry.setState(new SaveState(diaryEntry));
                }
            } else {
                throw new NullPointerException("Nessuna pagina di diario trovata con questo id");
            }
        } catch (NullPointerException e){
            throw e;
        }
        return diaryEntry;
    }

    /**
     * Recupera la pagina di diario richiesta e aggiorna i dati tramite l'oggetto newDiaryEntry passato in input.
     * @param diaryEntryId identificativo della pagina di diario
     * @param newDiaryEntry oggetto che contiene i dati provenienti dalla form di aggiornamento della nota di diario.
     * @return l'oggetto di classe DiaryEntry salvato.
     */
    public DiaryEntry findIdAndReplaceValues (int diaryEntryId, DiaryEntry newDiaryEntry) {
        DiaryEntry diaryEntry = diaryEntryRepository.findById(diaryEntryId);
        diaryEntry.setNote(newDiaryEntry.getNote());
        diaryEntry.setDiarrhea(newDiaryEntry.isDiarrhea());
        diaryEntry.setBloodStool(newDiaryEntry.isBloodStool());
        diaryEntry.setAbdominalPain(newDiaryEntry.isAbdominalPain());
        diaryEntry.setFatigue(newDiaryEntry.isFatigue());
        diaryEntry.setNumberOfBowelMovement(newDiaryEntry.getNumberOfBowelMovement());
        return diaryEntryRepository.save(diaryEntry);
    }

    /**
     * Salva la pagina di diario identificata con l'id in input come "Published"
     * @param id identificativo della pagina di diario ricercata
     * @return l'oggetto di classe DiaryInput appena modificato.
     */
    public DiaryEntry findDiaryEntryById(int id) throws NullPointerException{
        DiaryEntry newDiaryEntry;
        try {
            DiaryEntry diaryEntry = diaryEntryRepository.findById(id);
            diaryEntry.setEntryState("Published");
            newDiaryEntry = diaryEntryRepository.save(diaryEntry);
        } catch (NullPointerException e ){
            throw e;
        }
        return newDiaryEntry;
    }


     /** Recupera la nota di diario corrispondente per identificativo della nota stessa e del paziente (utente che l'ha
     * generata).
     * @param patientId identificativo del paziente che ha generato la nota.
     * @param id
     * @return restituisce la nota che corrisponde ai criteri di ricerca (id della nota e id del paziente che l'ha scritta)
     * @throws NullPointerException viene lanciata quando non viene trovato nessun record che corrisponda ai criteri di
      * ricerca.
     */
    public DiaryEntry checkDiaryEntryUser(int patientId, int id) throws NullPointerException{
        DiaryEntry diaryEntry;
        try {
            diaryEntry = diaryEntryRepository.findByIdAndPatientId(id, patientId);
            if(diaryEntry != null){
                // Se ha recuperato almeno una pagina di diario, inizializzare
                // l'attributo State in base all'attributo EntryState
                // Se è una bozza, cioè entryState == "Draft" allora
                // inizializzo settando un oggetto DraftState
                if(diaryEntry.getEntryState().equals("Draft")){
                    diaryEntry.setState(new DraftState(diaryEntry));
                    // Se è una bozza, cioè entryState == "Published" allora
                    // inizializzo settando un oggetto SaveState
                } else if(diaryEntry.getEntryState().equals("Published")){
                    diaryEntry.setState(new SaveState(diaryEntry));
                }
            } else {
                throw new NullPointerException("Utente loggato e nota di diario non sono compatibili.");
            }
        } catch (NullPointerException e){
            throw e;
        }
        return diaryEntry;
    }

    /**
     * cancellazione del record
     * @param id identificativo della pagina di diario che si vuole cancellare
     */
    public void deleteDraftDiaryEntry(int id){
        diaryEntryRepository.deleteById(id);
    }

    /**
     * Ricerca dell'utente che ha scritto la nota.
     * @param diaryEntryId identificativo della nota
     * @return oggetto di classe Patient che corrisponde all'utente a cui appartiene la nota di diario.
     */
    public Patient findPatientByDiaryEntryId(int diaryEntryId){
        return diaryEntryRepository.findPatientById(diaryEntryId);
    }

    /**
     * Ricerca del record riferito all'identificativo in input
     * @param diaryEntryId identificativo di una nota di diario.
     * @return un oggetto di classe DiaryEntry che corrisponde ai criteri di ricerca.
     * @throws NullPointerException viene lanciata questa eccezione se non viene trovato nessun record che corrisponde
     * ai criteri di ricerca indicati.
     */
    public DiaryEntry findDiaryEntryByDiaryId(int diaryEntryId) throws NullPointerException{
        DiaryEntry diaryEntry;
        try {
            diaryEntry = diaryEntryRepository.findById(diaryEntryId);
            if(diaryEntry == null){
                throw new NullPointerException("Nessuna nota di diario corrispondente ai criteri di ricerca");
            }
        } catch (NullPointerException e){
            throw e;
        }
        return diaryEntry;
    }

    /**
     * Ricerca di un record in base ai criteri di ricerca data della nota e identificativo del paziente a cui la nota
     * appartiene.
     * @param diaryEntryDate data inserita durante la creazione della nota, che rappresenta la data a cui fanno riferimento
     *                       le annotazioni inserite nella pagina di diario.
     * @param patientId identificativo del paziente che ha scritto la nota.
     * @return l'eventuale record trovato che corrisponde ai criteri di ricerca in input.
     */
    public DiaryEntry findByDateAndPatientId(Date diaryEntryDate, int patientId){
        return diaryEntryRepository.findByDateAndPatientId(diaryEntryDate, patientId);
    }

}
