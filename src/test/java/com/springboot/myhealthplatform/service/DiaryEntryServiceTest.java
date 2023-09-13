package com.springboot.myhealthplatform.service;

import com.springboot.myhealthplatform.board.bean.DiaryEntry;
import com.springboot.myhealthplatform.board.repository.DiaryEntryRepository;
import com.springboot.myhealthplatform.board.service.DiaryEntryService;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DiaryEntryServiceTest {

    @Mock
    private DiaryEntryRepository diaryEntryRepository;

    private AutoCloseable closeable;

    @InjectMocks
    private DiaryEntryService diaryEntryService;

    @BeforeEach
    void initService() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void closeService() throws Exception {
        closeable.close();
    }

    /**
     * -- SUPERATO --
     * Verifica che la pagina di diario venga salvata correttamente.
     *
     * @throws Exception
     */
    @Test
    public void it_should_save_a_diary_entry() throws Exception {
        DiaryEntry diaryEntry = new DiaryEntry();
        diaryEntry.setNote("Test note");
        diaryEntry.setAbdominalPain(false);
        diaryEntry.setFatigue(false);
        diaryEntry.setBloodStool(false);

        when(diaryEntryRepository.save(ArgumentMatchers.any(DiaryEntry.class))).thenReturn(diaryEntry);

        DiaryEntry diaryEntrySaved = diaryEntryService.save(diaryEntry);

        assertThat(diaryEntrySaved.getEntryState() == "Draft").isTrue();
    }

    /**
     * -- SUPERATO --
     * Verifica che vengano recuperati solo le pagine di diario con stato "Published".
     */
    @Test
    public void it_should_return_all_patient_s_published_diary_entry() {
        DiaryEntry diaryEntry1 = new DiaryEntry();
        diaryEntry1.setNote("Test note Uno");
        diaryEntry1.setEntryState("Published");
        diaryEntry1.setAbdominalPain(false);
        diaryEntry1.setFatigue(false);
        diaryEntry1.setBloodStool(false);
        DiaryEntry diaryEntry2 = new DiaryEntry();
        diaryEntry2.setNote("Test note Due");
        diaryEntry2.setEntryState("Published");
        diaryEntry2.setAbdominalPain(false);
        diaryEntry2.setFatigue(false);
        diaryEntry2.setBloodStool(false);
        int patientId = 1;
        List<DiaryEntry> diaryEntryList = new ArrayList<>();
        String stateValue = "Published";

        when(diaryEntryRepository.findAllByPatientIdAndEntryStateOrderByDateDesc(ArgumentMatchers.anyInt(), eq("Published"))).thenReturn(Arrays.asList(diaryEntry1, diaryEntry2));

        diaryEntryList = diaryEntryService.getAllPublishedDiaryEntries(patientId);

        assertThat(diaryEntryList.get(0).getNote()).isEqualTo(diaryEntry1.getNote());
        assertThat(diaryEntryList.get(1).getNote()).isEqualTo(diaryEntry2.getNote());
        System.out.println("TEST");
        System.out.println("Nota del primo oggetto della lista: " + diaryEntryList.get(0).getNote());
        System.out.println("Nota della pagina di diario Uno: " + diaryEntry1.getNote());
        System.out.println("Nota del secondo oggetto della lista: " + diaryEntryList.get(1).getNote());
        System.out.println("Nota della pagina di diario Due: " + diaryEntry2.getNote());
    }

    /**
     * -- SUPERATO --
     * Verifica che vengano recuperati solo le pagine di diario con stato "Draft".
     */
    @Test
    public void it_should_return_all_patient_s_draft_diary_entry() {
        DiaryEntry diaryEntry1 = new DiaryEntry();
        diaryEntry1.setNote("Test note Uno");
        diaryEntry1.setEntryState("Draft");
        diaryEntry1.setAbdominalPain(false);
        diaryEntry1.setFatigue(false);
        diaryEntry1.setBloodStool(false);
        int patientId = 1;
        List<DiaryEntry> diaryEntryList = new ArrayList<>();

        when(diaryEntryRepository.findAllByPatientIdAndEntryStateOrderByDateDesc(ArgumentMatchers.anyInt(), eq("Draft"))).thenReturn(Arrays.asList(diaryEntry1));

        diaryEntryList = diaryEntryService.getAllDraftDiaryEntries(patientId);

        assertThat(diaryEntryList.get(0).getNote()).isEqualTo(diaryEntry1.getNote());
        assertTrue(diaryEntryList.size() == 1);
        System.out.println("TEST");
        System.out.println("Nota del primo oggetto della lista: " + diaryEntryList.get(0).getNote());
        System.out.println("Nota della pagina di diario Uno: " + diaryEntry1.getNote());
        System.out.println("Lunghezza lista : " + diaryEntryList.size());
    }

    /**
     * -- SUPERATO --
     * Verifica che venga estratto la pagina di diario con id pari a quello usato come
     * criterio di ricerca.
     *
     * @throws Exception
     */
    @Test
    public void it_should_return_all_patient_s_diary_entries() throws Exception {
        DiaryEntry diaryEntry1 = new DiaryEntry();
        int diaryEntryId = 1;
        diaryEntry1.setId(diaryEntryId);
        diaryEntry1.setNote("Test note Uno Published");
        diaryEntry1.setEntryState("Published");
        diaryEntry1.setAbdominalPain(false);
        diaryEntry1.setFatigue(false);
        diaryEntry1.setBloodStool(false);
        DiaryEntry diaryEntry2 = new DiaryEntry();
        int diaryEntry2Id = 2;
        diaryEntry2.setId(diaryEntry2Id);
        diaryEntry2.setNote("Test note Due Draft");
        diaryEntry2.setEntryState("Draft");
        diaryEntry2.setAbdominalPain(false);
        diaryEntry2.setFatigue(false);
        diaryEntry2.setBloodStool(false);

        when(diaryEntryRepository.findById(ArgumentMatchers.anyInt())).thenReturn(diaryEntry1);

        DiaryEntry diaryEntrySaved = diaryEntryService.getDiaryEntryDraftById(diaryEntryId);

        assertTrue(diaryEntrySaved.getId() == diaryEntry1.getId());

        System.out.println("TEST");
        System.out.println("Id dell'oggetto estratto: " + diaryEntrySaved.getId());
        System.out.println("Id dell'oggetto in db: " + diaryEntry1.getId());
    }

    /**
     * -- SUPERATO --
     * Verifica che venga lanciata l'eccezione NullPointException se non viene trovata nessuna
     * corrispondenza con i criteri di ricerca.
     *
     * @throws Exception
     */
    @Test
    public void it_should_return_an_error_because_no_diary_entry_found() throws Exception {
        int diaryEntryId = 1;

        when(diaryEntryRepository.findById(diaryEntryId)).thenReturn(null);;

        assertThrows(Exception.class, () -> diaryEntryService.getDiaryEntryDraftById(diaryEntryId));
        assertThrows(Exception.class, () -> diaryEntryService.getDiaryEntryDraftById(diaryEntryId), "Nessuna pagina di diario trovata con questo id");

    }

    /**
     * -- SUPERATO --
     * Verifica che vengano modificati correttamente i valori della pagina di diario, recuperata
     * tramite il suo Id.
     *
     * @throws Exception
     */
    @Test
    public void it_should_replace_values() throws Exception {
        // Creo due oggetti uguali
        // diaryEntryPreModifica non verrà modificato
        // diaryEntryDaModificare verrà modificato e poi salvato nella repository
        DiaryEntry diaryEntryPreModifica = new DiaryEntry();
        int diaryEntryId = 2;
        diaryEntryPreModifica.setId(diaryEntryId);
        diaryEntryPreModifica.setNote("Test note Due Published");
        diaryEntryPreModifica.setEntryState("Draft");
        diaryEntryPreModifica.setAbdominalPain(false);
        diaryEntryPreModifica.setFatigue(false);
        diaryEntryPreModifica.setBloodStool(false);
        DiaryEntry diaryEntryDaModificare = new DiaryEntry();
        int diaryEntry2Id = 2;
        diaryEntryDaModificare.setId(diaryEntry2Id);
        diaryEntryDaModificare.setNote("Test note Due Published");
        diaryEntryDaModificare.setEntryState("Draft");
        diaryEntryDaModificare.setAbdominalPain(false);
        diaryEntryDaModificare.setFatigue(false);
        diaryEntryDaModificare.setBloodStool(false);

        when(diaryEntryRepository.findById(ArgumentMatchers.anyInt())).thenReturn(diaryEntryDaModificare);

        diaryEntryDaModificare.setNote("Test note Modificate");

        when(diaryEntryRepository.save(diaryEntryDaModificare)).thenReturn(diaryEntryDaModificare);

        DiaryEntry diaryEntrySaved = diaryEntryService.findIdAndReplaceValues(diaryEntryDaModificare.getId(), diaryEntryDaModificare);

        assertThat(diaryEntrySaved.getNote().equals(diaryEntryPreModifica.getNote())).isFalse();
        assertThat(diaryEntrySaved.getNote().equals(diaryEntryDaModificare.getNote())).isTrue();

        System.out.println("TEST");
        System.out.println("Note dell'oggetto estratto tramite findById: " + diaryEntryPreModifica.getNote());
        System.out.println("Note dell'oggetto salvato nel db: " + diaryEntryDaModificare.getNote());
        System.out.println("Note dell'oggetto restituito dal salvataggio: " + diaryEntrySaved.getNote());

    }

    /**
     * -- SUPERATO --
     * Modifica lo stato della pagina di diario da Draft a Published.
     *
     * @throws Exception
     */
    @Test
    public void it_should_change_the_diary_entry_state() throws Exception {
        DiaryEntry diaryEntry1 = new DiaryEntry();
        int diaryEntryId = 2;
        diaryEntry1.setId(diaryEntryId);
        diaryEntry1.setNote("Test note Uno Draft");
        diaryEntry1.setEntryState("Draft");
        diaryEntry1.setAbdominalPain(false);
        diaryEntry1.setFatigue(false);
        diaryEntry1.setBloodStool(false);

        when(diaryEntryRepository.findById(ArgumentMatchers.anyInt())).thenReturn(diaryEntry1);
        DiaryEntry diaryEntry2 = diaryEntryRepository.findById(diaryEntry1.getId());
        when(diaryEntryRepository.save(diaryEntry1)).thenReturn(diaryEntry2);

        DiaryEntry diaryEntrySaved = diaryEntryService.findDiaryEntryById(diaryEntry1.getId());
        System.out.println("Stato del diaryEntry: " + diaryEntrySaved.getEntryState());

        assertTrue(diaryEntrySaved.getEntryState() == "Published");

    }

    /** -- SUPERATO --
     * Verifica che venga inserito correttamente lo State corrispondente allo stato (attributo
     * EntryState) registrato nel DB.
     * @throws Exception
     */
    @Test
    public void it_should_check_the_diary_patient_s_user() throws Exception {
        DiaryEntry diaryEntry1 = new DiaryEntry();
        int diaryEntryId = 2;
        diaryEntry1.setId(diaryEntryId);
        diaryEntry1.setNote("Test note Uno Draft");
        diaryEntry1.setEntryState("Draft");
        diaryEntry1.setAbdominalPain(false);
        diaryEntry1.setFatigue(false);
        diaryEntry1.setBloodStool(false);
        int patientId = 1;

        when(diaryEntryRepository.findByIdAndPatientId(diaryEntryId, patientId)).thenReturn(diaryEntry1);
        DiaryEntry diaryEntry2 = diaryEntryService.checkDiaryEntryUser(patientId, diaryEntryId);

        assertTrue(diaryEntry1.getState() == diaryEntry2.getState());

        System.out.println("TEST");
        System.out.println(diaryEntry2.getState());
        System.out.println(diaryEntry1.getState());

    }

    /** -- SUPERATO --
     * Verifica che, se non viene recuperato nessuna pagina di diario per il paziente, venga lanciata
     * una eccezione.
     * @throws Exception
     */
    @Test
    public void it_should_retunr_an_exception_during_the_check() throws Exception {
        DiaryEntry diaryEntry1 = new DiaryEntry();
        int diaryEntryId = 2;
        diaryEntry1.setId(diaryEntryId);
        diaryEntry1.setNote("Test note Uno Draft");
        diaryEntry1.setEntryState("Draft");
        diaryEntry1.setAbdominalPain(false);
        diaryEntry1.setFatigue(false);
        diaryEntry1.setBloodStool(false);
        int patientId = 1;

        // Ritorna NULL perchè non c'è nessuna corrispondenza //
        when(diaryEntryRepository.findByIdAndPatientId(diaryEntryId, patientId)).thenReturn(null);

        assertThrows(Exception.class, () -> diaryEntryService.checkDiaryEntryUser(patientId, diaryEntryId));
        assertThrows(Exception.class, () -> diaryEntryService.checkDiaryEntryUser(patientId, diaryEntryId), "Utente loggato e nota di diario non sono compatibili.");
    }

    /** -- SUPERATO --
     * Verifica che, se non viene recuperato nessuna pagina di diario, venga lanciata una eccezione.
     * @throws Exception
     */
    @Test
    public void it_should_return_an_exception_because_diary_entry_id_doesnt_exist() throws Exception {
        int diaryEntryId =1;
        when(diaryEntryRepository.findById(diaryEntryId)).thenReturn(null);
        assertThrows(Exception.class, () -> diaryEntryService.findDiaryEntryByDiaryId(diaryEntryId));
        assertThrows(Exception.class, () -> diaryEntryService.findDiaryEntryByDiaryId(diaryEntryId), "Nessuna nota di diario corrispondente ai criteri di ricerca");
    }

}
