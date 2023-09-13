package com.springboot.myhealthplatform.board.designPattern;

import com.springboot.myhealthplatform.board.bean.DiaryEntry;

/**
 * Interfaccia che riporta tutti i metodi da implementare nelle classi concrete del
 * State design pattern.
 */
public interface State {

    /**
     * Salva la pagina di diario come bozza.
     * @param diaryEntry pagina di diario da salvare
     */
    void saveDocument(DiaryEntry diaryEntry);

    /**
     * Il documento viene pubblicato
     * @param diaryEntry pagina di diario che si sta tentando di pubblicare
     */
    void uploadDocument(DiaryEntry diaryEntry);

    /**
     * La pagina di diario viene eliminata
     * @param diaryEntry pagina di diario da eliminare
     */
    void deleteDraft(DiaryEntry diaryEntry);
}
