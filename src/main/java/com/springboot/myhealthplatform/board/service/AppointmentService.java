package com.springboot.myhealthplatform.board.service;

import com.springboot.myhealthplatform.bean.Patient;
import com.springboot.myhealthplatform.board.bean.Appointment;
import com.springboot.myhealthplatform.board.bean.ExamCategory;
import com.springboot.myhealthplatform.board.repository.AppointmentRepository;
import com.springboot.myhealthplatform.board.repository.ExamCategoryRepository;
import com.springboot.myhealthplatform.repository.PatientRepository;
import org.hibernate.service.NullServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Classe che comunica con le classi Repository per estrarre o registrare dati provenienti dai Controller.
 * Service dedicato alla gestione degli appuntamenti (Appointment class)
 */
@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private ExamCategoryRepository examCategoryRepository;
    @Autowired
    private PatientRepository patientRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, ExamCategoryRepository examCategoryRepository, PatientRepository patientRepository){
        this.appointmentRepository = appointmentRepository;
        this.examCategoryRepository = examCategoryRepository;
        this.patientRepository = patientRepository;
    }

    /**
     * Passa i parametri provenienti dal controller alla classe AppointmentRepository
     * per salvare i dati dell'oggetto Appointment provenienti dalla form.
     * @param appointment oggetti che contiene tutti i dati riferiti all'appuntamento
     *                    che si vuole salvare sul database.
     * @return l'oggetto di tipo Appointment che è stato salvato.
     */
    public Appointment save(Appointment appointment) throws NullPointerException, Exception {
        try {
            ExamCategory examCategory = examCategoryRepository.findByCategoryTitle(appointment.getExamCategory().getCategoryTitle());
            if(examCategory == null){
                throw new NullPointerException("Ruolo non esistente");
            }
            if(examCategory.getCategoryTitle().equals("YOUR_SPECIALIST")){
                appointment.setDoctor(appointment.getPatient().getDoctor());
            }
            appointment.setExamCategory(examCategory);
        } catch(NullPointerException e){
            throw e;
        }
        return appointmentRepository.save(appointment);
    }

    /**
     * L'appuntamento creato dal medico viene salvato nel database, dopo aver verificato che il ruolo
     * sia esistente e il paziente a cui associare l'appuntamento esista e sia un paziente del medico.
     * @param appointment oggetto di tipo Appointment derivante dalla form.
     * @return
     * @throws Exception
     */
    public Appointment saveDoctorAppointment(Appointment appointment, int doctorId) throws NullPointerException{
        try {
            ExamCategory examCategory = examCategoryRepository.findById(4); // YOUR_SPECIALIST
            if(examCategory == null){
                throw new NullPointerException("Ruolo non esistente");
            } else {
                appointment.setExamCategory(examCategory);
                Patient patient = patientRepository.findById(appointment.getPatient().getId());
                //Se non è stato recuperato nessun paziente oppure il dottore associato al paziente non è
                // il medico che sta creando l'appuntamento, lancia l'eccezione.
                if(patient == null || doctorId != patient.getDoctor().getId()){
                    throw new NullPointerException("Il paziente non esiste o l'utente non è il medico del paziente selezionato.");
                } else {
                    appointment.setPatient(patient);
                }
            }
        } catch (NullPointerException e){
            throw e;
        }
        return appointmentRepository.save(appointment);
    }

    /** -- NOT USED --
     * Recupera tutti gli appuntamenti del medico indicato
     * @param doctorId identificativo del medico.
     * @return la lista di appuntamentamenti di oggi
     */
    public List<Appointment> getAllAppointmentByDoctorIdAndDateToday(int doctorId){
        // NON TROVA PERCHE' PRENDE L'ORARIO DI ADESSO
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/mm/DD");
        Date date = new Date();
        return appointmentRepository.findAllByDoctorIdAndAppointmentDate(doctorId, date);
    }

    /**
     * Richiede alla repository dell'oggetto Appointment tutti i record riferiti
     * al paziente richiesto.
     * @param patientId variabile di tipo int che corrisponde all'id del paziente
     *                  di cui si stanno richiedendo gli appuntamenti
     * @return gli oggetti di classe Appointment raccolti in una Lista.
     */
    public List<Appointment> getAllAppointmentsByPatientId(int patientId){
        List<Appointment> appointments = appointmentRepository.findAllByPatientIdOrderByAppointmentDateAsc(patientId);
        LocalDateTime today = null;
        List<Appointment> futureAppointments = new ArrayList<>();
        for(Appointment a : appointments){
            // Mostrare solo se l'appuntamento è nel futuro,
            // quindi se è in questo anno, il giorno deve essere successivo ad oggi
            // altrimenti l'anno deve essere successivo a quest'anno.
            if(((a.getAppointmentDate().getYear() == today.now().getYear()) && (a.getAppointmentDate().getDayOfYear() >= today.now().getDayOfYear())) || (a.getAppointmentDate().getYear()> today.now().getYear())){
                futureAppointments.add(a);
            }
        }
        return futureAppointments;
    }

    /**
     * Richiede alla repository della classe Appointment tutti i record riferiti
     * al dottore richiesto (che corrisponde a quello loggato).
     * @param doctorId variabile di tipo int che corrisponde all'id del paziente
     *                 di cui si stanno richiedendo gli appuntamenti.
     * @return gli oggetti di classe Appointment che corrispondono ai parametri,
     *          raccolti in una lista.
     */
    public List<Appointment> getAllAppointmentByDoctorId(int doctorId){
        LocalDateTime today = null;
        List<Appointment> appointments = appointmentRepository.findAllByDoctorIdOrderByAppointmentDateAsc(doctorId);
        List<Appointment> futureAppointments = new ArrayList<>();
        // Mostrare solo se l'appuntamento è nel futuro,
        // quindi se è in questo anno, il giorno deve essere successivo ad oggi
        // altrimenti l'anno deve essere successivo a quest'anno.
        for(Appointment a : appointments){
            if(((a.getAppointmentDate().getDayOfYear() >= today.now().getDayOfYear()) && a.getAppointmentDate().getYear()== today.now().getYear()) || a.getAppointmentDate().getYear() > today.now().getYear()){
                futureAppointments.add(a);
            }
        }
        return futureAppointments;
    }

    /**
     * Cancella un appuntamento in base al suo Id
     * @param appointmentId identificativo dell'appuntamento
     */
    public void deleteAppointment(int appointmentId){
        appointmentRepository.deleteById(appointmentId);
    }

    /**
     * Dati l'id di un appuntamento e l'id del paziente, il metodo verifica che l'appuntamento sia effettivamente
     * del paziente indicato e lo recupera per passarlo al controller. Se il risultato desse esito null, allora
     * verrebbe lanciata una eccezione.
     * @param patientId identificativo del paziente
     * @param appointmentId identificativo di un appuntamento.
     * @return l'oggetto appointment corrispondente all'appointmentId in input.
     * @throws NullPointerException viene lanciato se il risultato della ricerca nel DB è null.
     */
    public Appointment checkAppointmentUser(int patientId, int appointmentId) throws NullPointerException {
        Appointment appointment;
        try {
            appointment = appointmentRepository.findByIdAndPatientId(appointmentId, patientId);
            if (appointment == null) {
                throw new NullPointerException("Nessun appuntamento trovato con questo id per questo paziente");
            }
        } catch (NullPointerException e) {
            throw e;
        }
        return appointment;
    }

    /**
     * Recupera tutti gli appuntamenti di oggi del medico il cui id viene passato in input.
     * @param doctorId identificativo del medico.
     * @return la lista di appuntamenti di oggi. La lista può anche essere null.
     */
    public List<Appointment> getAllTodayAppointmentsByDoctorId(int doctorId){
        LocalDateTime today = null;
        List<Appointment> appointments = new ArrayList<>();
        appointments = appointmentRepository.findAllByDoctorIdOrderByAppointmentDateAsc(doctorId);
        List<Appointment> todayAppointments = new ArrayList<>();
        //Per ogni appuntamento recuperato, valuto se la data è oggi.
        // Se la data è diversa da oggi, scarto l'oggetto, altrimenti lo salvo nella lista todayAppointments.
        for(Appointment a : appointments){
            if((a.getAppointmentDate().getDayOfYear() == today.now().getDayOfYear()) && a.getAppointmentDate().getYear()== today.now().getYear()){
                todayAppointments.add(a);
            }
        }
        return todayAppointments;
    }

}

