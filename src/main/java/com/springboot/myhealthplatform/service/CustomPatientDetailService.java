package com.springboot.myhealthplatform.service;

import com.springboot.myhealthplatform.bean.*;
import com.springboot.myhealthplatform.login.CustomUserDetailsService;
import com.springboot.myhealthplatform.repository.DoctorRepository;
import com.springboot.myhealthplatform.repository.PatientRepository;
import com.springboot.myhealthplatform.repository.RoleRepository;
import com.springboot.myhealthplatform.repository.UserRepository;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Service;

import java.util.*;
/**
 * Classe che comunica con le classi Repository per estrarre o registrare dati provenienti dai Controller.
 * Service dedicato alla gestione dell'utente paziente (classe Patient).
 */
@Service
public class CustomPatientDetailService {

    private CustomUserDetailsService customUserDetailService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public CustomPatientDetailService(CustomUserDetailsService customUserDetailsService, UserRepository userRepository, RoleRepository roleRepository, DoctorRepository doctorRepository, PatientRepository patientRepository){
        this.customUserDetailService = customUserDetailsService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    /**
     * Metodo che salva l'oggetto user collegato al paziente e poi salva sul database il record riferito
     * al paziente.
     * @param userRegistrationDto strumento che porta dal layer del controller le variabili utili per registrare
     *                            un nuovo record sulla tabella user.
     * @param patientRegistrationDto strumento che porta dal layer del controller le variabili utili per registrare
     *      *                            un nuovo record sulla tabella patient.
     * @return l'oggetto Patient appena salvato.
     */
    public Patient save(UserRegistrationDto userRegistrationDto, PatientRegistrationDto patientRegistrationDto){

        User patientUser;
        // salvo i dati che verranno gestiti dalla classe User
        patientUser = customUserDetailService.save(userRegistrationDto);
        // Recupero le informazioni sul medico scelto
        Doctor matchedDoctor = doctorRepository.findById(patientRegistrationDto.getDoctorId());
        // Inserisco i dati recuperati dalla form in un oggetto di classe Patient e salvo.
        Patient patient = new Patient(patientRegistrationDto.getName(),
                patientRegistrationDto.getSurname(),
                patientRegistrationDto.getTelephone(),
                patientRegistrationDto.getCF(),
                patientRegistrationDto.getDOB(),
                patientRegistrationDto.getAddress(),
                patientRegistrationDto.getCityOfBirth(),
                matchedDoctor,
                patientUser);

        return patientRepository.save(patient);
    }

    /**
     *  Recupera una lista di tutti i pazienti registrati nel database
     * @return lista dei pazienti presenti nel database ordinati per cognome
     * @throws NullPointerException se non ci sono pazienti nel database.
     */
    public List<Patient> getAll() throws NullPointerException {
        List<Patient> patients = patientRepository.findAllByOrderBySurname();
        if(patients == null){
            throw new NullPointerException("Nessun paziente registrato nell'applicativo");
        }
        return patients;
    }


    /**
     * La stringa userUsername viene usata per recuperare i dati dell'oggetto User. Con l'id dell'oggetto User
     * ricerco il paziente (associazione OneToOne tra User e Patient quindi avrò un risultato univoco).
     * Se la ricerca dello User o la ricerca del Patient non dovessero dare esito (quindi la ricerca restituisce
     * il valore "null") verrà lanciata una eccezione di tipo NullPointException, gestita dal controller.
     * @param userUsername equivale al valore Pricipal.getName() riferito allo username dell'utente loggato.
     * @return l'oggetto Patient che contiene tutti i dati riferiti al paziente.
     * @throws NullPointerException se non viene trovato nessun User corrispondente a quel username oppure
     * nessun Patient legato all'id dello User trovato nel database.
     */
    public Patient findPatientByUserUsername(String userUsername) throws NullPointerException{
        try{
            // Cerco lo user tramite username
            User user = userRepository.findUserByUsername(userUsername);
            // Verifico che abbia recuperato l'utente, altrimenti lancio l'eccezione
            if(user == null){
                throw new NullPointerException("User not found");
            }
            // Cerco l'oggetto patient tramite l'id dello user.
            Patient patient = patientRepository.findByUserId(user.getId());
            // Verifico che abbia recuperato l'utente-paziente, altrimenti lancio una accezione.
            if(patient == null){
                throw new NullPointerException("Patient not found");
            }
            // Ritorno l'oggetto patient
            return patient;
        } catch(NullPointerException e){
            // lancio l'eccezione che viene inviata al controller.
            throw new NullPointerException(e.getMessage());
        }
    }

    /**
     * Tramite l'id ddi un oggetto patient, possiamo recuperare l'intero oggetto.
     * @param patientId identificativo di un oggetto Patient
     * @return un oggetto Patient
     * @throws NullPointerException viene lanciato se il risultato della ricerca nel DB dà esito negativo.
     */
    public Patient findPatientById(int patientId) throws NullPointerException{
        Patient patient = patientRepository.findById(patientId);
        if(patient == null){
            throw new NullPointerException("Nessun paziente trovato con questo id");
        }
        return patient;
    }

    /**
     * Tramite l'id del medico, recupero tutti i pazienti che sono seguiti dal medico stesso.
     * @param doctorId identificativo dell'oggetto Doctor.
     * @return la lista di pazienti seguiti dall'utente medico il cui id è usato per la ricerca.
     * La ricerca può dare esito null se al medico non sono stati ancora associati dei pazienti.
     */
    public List<Patient> findAllByDoctorId(int doctorId){
        return patientRepository.findByDoctorId(doctorId);
    }

    /**
     * Ricerca di uno specifico oggetto Patient tramite il suo id (patientId) e l'id dell'oggetto doctor.
     * Si sta, quindi, cercando un paziente tramite il suo codice identificativo e tramite l'id del medico che
     * lo ha in cura.
     * @param patientId identificativo dell'oggetto patient.
     * @param doctorId identificativo dell'oggetto doctor.
     * @return un unico oggetto di tipo Optional<Patient> se presente. Altrimenti ritorna null.
     */
    public Optional<Patient> findByIdAndDoctorId(int patientId, int doctorId){
        return patientRepository.findByIdAndDoctorId(patientId, doctorId);
    }

    /**
     * Ricerca di un oggetto patient tramite l'attributo CF.
     * Il codice fiscale deve essere univoco, quindi, se durante la registrazione, si inserisce un CF
     * già esistente nel database, il nuovo utente non può essere registrato. Il caso avviene, se dalla
     * ricerca nel patientRepository il risultato non è null.
     * @param CF variabile univoca tramite la quale avviene la ricerca di un oggetto patient nel DB.
     * @throws ValidationException viene lanciata se viene trovato un record corrispondente alla chiave di
     * ricerca (CF).
     */
    public void findCFForValidation(String CF) throws ValidationException{
        if(patientRepository.findByCF(CF) != null){
            throw new ValidationException("CF is already used. Patient already presents in the DB.");
        }
    }

}
