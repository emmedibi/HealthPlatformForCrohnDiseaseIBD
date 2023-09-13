package com.springboot.myhealthplatform.board.designPattern;

import com.springboot.myhealthplatform.board.bean.DiaryEntry;

/**
 * Classe che rappresenta lo stato "Published" di una pagina di diario.
 * Implementa l'interfaccia State
 */
public class SaveState implements State{
    // Documento salvato come Pubblicato

    private DiaryEntry diaryEntry;

    /**
     * Il documento non può essere modificato e risalvato, in quanto già pubblicato.
     * @param diaryEntry
     */
    @Override
    public void saveDocument(DiaryEntry diaryEntry) {
        String message = "Documento gia' salvato nel database. Operazione non valida.";
        throw new IllegalStateException(message);
    }

    /**
     * La pagina di diario non può essere salvata nuovamente.
     * @param diaryEntry
     */

    @Override
    public void uploadDocument(DiaryEntry diaryEntry) {
        String message = "Documento gia' salvato. Operazione non valida.";
        throw new IllegalStateException(message);
    }

    /**
     * Non è possibile eliminare una pagina di diario se già pubblicata.
     * @param diaryEntry
     */
    @Override
    public void deleteDraft(DiaryEntry diaryEntry) {
        String message = "Documento gia' salvato. Operazione non valida.";
        throw new IllegalStateException(message);
    }

    public SaveState(DiaryEntry diaryEntry){
        this.diaryEntry = diaryEntry;
    }

}
