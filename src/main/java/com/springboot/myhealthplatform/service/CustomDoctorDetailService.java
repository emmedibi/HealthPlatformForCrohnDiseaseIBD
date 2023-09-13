package com.springboot.myhealthplatform.service;

import com.springboot.myhealthplatform.bean.*;
import com.springboot.myhealthplatform.login.CustomUserDetailsService;
import com.springboot.myhealthplatform.repository.DoctorRepository;
import com.springboot.myhealthplatform.repository.RoleRepository;
import com.springboot.myhealthplatform.repository.UserRepository;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Service;

import java.util.List;
/**
 * Classe che comunica con le classi Repository per estrarre o registrare dati provenienti dai Controller.
 * Service dedicato alla gestione dell'utente medico (classe Doctor)
 */
@Service
public class CustomDoctorDetailService {
    private CustomUserDetailsService customUserDetailsService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DoctorRepository doctorRepository;


    public CustomDoctorDetailService(CustomUserDetailsService customUserDetailsService, UserRepository userRepository, RoleRepository roleRepository, DoctorRepository doctorRepository){
        this.customUserDetailsService = customUserDetailsService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.doctorRepository = doctorRepository;
    }

    /**
     * Il metodo salva l'oggetto Doctor nel database collegato a DoctorRepository. Prima di salvare l'utenza del medico,
     * procediamo a salvare i dati riferiti ad username, email e password nel database User.
     * @param registrationDto
     * @param doctorRegistrationDto
     * @return
     */
    public Doctor save(UserRegistrationDto registrationDto, DoctorRegistrationDto doctorRegistrationDto) {
        User doctorUser;
        // Salva i dati del medico che saranno salvati nella tabella user
        doctorUser = customUserDetailsService.save(registrationDto);
        // Creo una istanza della classe Doctor e ne aggiungo lo User appena creato.
        Doctor doctor = new Doctor(
                doctorRegistrationDto.getName(),
                doctorRegistrationDto.getSurname(),
                doctorRegistrationDto.getTelephone(),
                doctorRegistrationDto.getCF(),
                doctorRegistrationDto.getBadgeNumber(),
                doctorUser
        );
        return doctorRepository.save(doctor);
    }

    /**
     * Recupera tutti i medici presenti nel database
     * @return una lista di oggetti Doctor corrispondenti a tutti i medici salvati nel database.
     * @throws NullPointerException viene lanciata l'eccezione se non viene trovato nessun medico salvato
     * nel database.
     */
    public List<Doctor> getAll() throws NullPointerException {
        List<Doctor> doctors = doctorRepository.findAllByOrderBySurname();
        if(doctors == null){
            throw new NullPointerException("Nessun dottore registrato nell'applicativo");
        }
        return doctors;
    }


    /**
     * Trova il medico avendo in input il suo Username.
     * @param userUsername lo username che identifica un utente.
     * @return restituisce un oggetto Doctor oppure lancia una eccezione.
     */
    public Doctor findDoctorByUsername(String userUsername) throws NullPointerException, Exception{
        try {
            // Cerco l'istanza User tramite il suo username
            User user = userRepository.findUserByUsername(userUsername);
            if(user == null){
                throw new NullPointerException("Nessun utente trovato con questo username");
            }
            // Cerco il medico avendo l'id dello User ad esso collegato.
            Doctor doctor = doctorRepository.findByUserId(user.getId());
            if(doctor == null){
                throw new NullPointerException("Nessun utente con ruolo medico trovato con questo username");
            }
            return doctor;
        } catch (NullPointerException e){
            throw new NullPointerException(e.getMessage());
        } catch(Exception e){
            throw new Exception("E' avvenuto un errore durante l'operazione di ricerca nel database.");
        }
    }

    /**
     * Verifica esistenza di un record con un determinato CF: se esiste lancio una eccezione
     * per fermare la creazione di un nuovo utente con stesso CF.
     * @param CF : codice fiscale inserito tramite form
     * @throws ValidationException : lancio l'eccezione se esiste già un record con questo Codice Fiscale
     */
    public void findCFForValidation(String CF) throws ValidationException{
        if(doctorRepository.findByCF(CF) != null){
            throw new ValidationException("CF is already used. Patient already presents in the DB.");
        }
    }

    /**
     * Verifica esistenza di un record con un determinato badgeNumber: se esiste lancio una eccezione
     * @param badgeNumber codice del badge del medico, inserito nella form di registrazione.
     * @throws ValidationException lancio l'eccezione se esiste già un record con questo numero di Badge.
     */
    public void findBadgeNumberForValidation(String badgeNumber) throws ValidationException{
        if(doctorRepository.findByBadgeNumber(badgeNumber) != null){
            throw new ValidationException("Badge Number is already used. Patient already presents in the DB.");
        }
    }

}
