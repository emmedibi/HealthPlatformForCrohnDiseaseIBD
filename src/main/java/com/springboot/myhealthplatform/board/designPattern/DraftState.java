package com.springboot.myhealthplatform.board.designPattern;

import com.springboot.myhealthplatform.board.bean.DiaryEntry;
/**
 * Classe che rappresenta lo stato "Draft" di una pagina di diario.
 * Implementa l'interfaccia State
 */
public class DraftState implements State{
    // Documento salvato come bozza

    private DiaryEntry diaryEntry;

    /**
     * Il documento viene salvato, rimandendo in stato di Bozza ("Draft")
     * @param diaryEntry
     */
    @Override
    public void saveDocument(DiaryEntry diaryEntry) {
        // Il documento rimane in questo stato
    }

    /**
     * L'oggetto diaryEntry viene pubblicato, quindi viene cambiato sia il valore del suo attributo
     * entryState sia il suo stato in SaveState()
     * @param diaryEntry
     */
    @Override
    public void uploadDocument(DiaryEntry diaryEntry) {
        diaryEntry.setEntryState("Published");
        diaryEntry.setState(new SaveState(diaryEntry));
    }

    /**
     * L'oggetto diaryEntry viene cancellato, quindi perde ogni stato.
     * @param diaryEntry
     */
    @Override
    public void deleteDraft(DiaryEntry diaryEntry){}


    public DraftState(DiaryEntry diaryEntry){
        this.diaryEntry = diaryEntry;
    }
}
