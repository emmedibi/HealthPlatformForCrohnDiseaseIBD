package com.springboot.myhealthplatform.login;

import com.springboot.myhealthplatform.bean.Role;
import com.springboot.myhealthplatform.bean.User;
import com.springboot.myhealthplatform.bean.UserRegistrationDto;
import com.springboot.myhealthplatform.repository.RoleRepository;
import com.springboot.myhealthplatform.repository.UserRepository;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
/**
 * Classe che implementa l'interfaccia UserDetailsService, che permette di gestire l'autenticazione
 * dell'utente.
 * L'autenticazione avviene tramite username e password.
 */

@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;
  private RoleRepository roleRepository;
  @Autowired
  private BCryptPasswordEncoder passwordEncoder;

  /**
   * Construttore della classe.
   *
   * @param userRepository repository della classe User
   * @param roleRepository repository della classe Role
   */
  public CustomUserDetailsService(UserRepository userRepository, RoleRepository roleRepository) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
  }

  /**
   * Metodo utile al login dell'utente. L'utente inserisce il proprio Username e il sistema verifica che esso sia
   * presente all'interno del database (repository della classe User). Se lo username non è presente, allora viene
   * lanciata l'eccezione UsernameNotFoundException che riporta il messaggio "User not found" alla pagina di login.
   * Viene chiamato dal metodo authenticationProvider del file SecSecutiryConfig
   * @param usernameOrEmail the username identifying the user whose data is required.
   * @return un'istanza di UserDetails, che contiene i dati dell'utente loggato.
   * @throws UsernameNotFoundException
   */
  @Override
  public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
    User user = userRepository.findUserByUsername(usernameOrEmail);
    if (user != null) {
      // ritorna un oggetto User del package UserDetails che permette la gestione dell'autenticazione dell'utente.
      return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), mapRolesToAuthorities(user.getRoles()));
    } else {
      throw new UsernameNotFoundException("User not found");
    }
  }

  /**
   * Registrazione di un nuovo utente all'interno del DB. Confermato che il ruolo selezionato esiste,
   * viene creato un oggetto User a partire dall'oggetto registrationDto, che poi viene salvato nel database.
   *
   * @param registrationDto oggetto che contiene tutte le informazioni utili per creare un nuovo user, provenienti
   *                        dalla form di registrazione.
   * @return l'oggetto User salvato nel Database.
   */
  public User save(UserRegistrationDto registrationDto) {
    Role role = roleRepository.findByName(registrationDto.getRole());
    // Se il ruolo non esiste, crealo (guarda metodo più sotto)
    if (role == null) {
      role = checkRoleExist(registrationDto.getRole());
    }
    User user = new User(
            registrationDto.getUsername(),
            registrationDto.getName(),
            registrationDto.getSurname(),
            registrationDto.getEmail(),
            passwordEncoder.encode(registrationDto.getPassword()),
            Arrays.asList(role));
    return userRepository.save(user);
  }

  /**
   * Ritorna la mappatura del ruoli degli utenti.
   * @param roles
   * @return
   */
  private Collection<? extends GrantedAuthority>
  mapRolesToAuthorities(Collection<Role> roles) {

    return roles.stream()
            .map(role -> new SimpleGrantedAuthority
                    (role.getName()))
            .collect(Collectors.toList());
  }


  /**
   * Recupera tutti gli oggetti di tipo User presenti nel database.
   * Se non ce ne fossero, il sistema restituisce la lista come null "null".
   *
   * @return lista di oggetti di tipo User
   */
  public List<User> getAll() {
    return userRepository.findAll();
  }

  /**
   * Trova un utente (oggetto User) in base al suo Username.
   *
   * @param username parametro per la ricerca. E' lo username con cui è stato registrato un utente.
   * @return l'oggetto User, se presente.
   */
  public User findByUsername(String username) {
    return userRepository.findUserByUsername(username);
  }

  /**
   * Verifica che il ruolo esista. Se il ruolo non esiste, allora lo crea ex novo.
   * In partenza non esiste nessun ruolo già configurato.
   * @param registrationRole
   * @return
   */
  private Role checkRoleExist(String registrationRole) {
    Role role = new Role();
    role.setName(registrationRole);
    return roleRepository.save(role);
  }

  /**
   * Trova tutti i pazienti all'interno del database degli user, in base al codice del Ruolo.
   *
   * @param roleId
   * @return
   */
  public List<User> findAllPatients(Integer roleId) {
    return userRepository.findUsersByRolesId(roleId);
  }


  /**
   * Recupera l'oggetto User con ruolo ROLE_NURSE, in base ad una variabile di tipo Stringa,
   * chiamata Username.
   * @param username parametro per la ricerca. E' lo username con cui è stato registrato un utente.
   * @return l'oggetto User, se trovato.
   * @throws UsernameNotFoundException nel caso la ricerca dia esito negativo e non si trovi nessun oggetto di tipo
   *                                   User con ruolo ROLE_NURSE
   */
  public User findNurseByUsername(String username) throws UsernameNotFoundException {
    User nurse = userRepository.findUserByUsername(username);
    // Se il risultato della ricerca nel database è diverso da null, allora verifica che il ruolo
    // dello user estratto sia NURSE
    if (nurse != null) {
      for (Role r : nurse.getRoles()) {
        if (r.getName().equals("ROLE_NURSE")) {
          return nurse;
        } else throw new UsernameNotFoundException(("Infermiere non trovato con questo username"));
      }
    }
    return nurse;
  }

  /**
   * Recupera l'oggetto User con ruolo ROLE_DOCTOR, in base ad una variabile di tipo Stringa,
   * chiamata Username.
   * @param username parametro per la ricerca. E' lo username con cui è stato registrato un utente.
   * @return l'oggetto User, se trovato.
   * @throws UsernameNotFoundException nel caso la ricerca dia esito negativo e non si trovi nessun oggetto di tipo
   *                                   * User con ruolo ROLE_DOCTOR
   */
  public User findDoctorByUsername(String username) throws UsernameNotFoundException {
    User doctor = userRepository.findUserByUsername(username);
    if (doctor != null) {
      // Se il risultato della ricerca nel database è diverso da null, allora verifica che il ruolo
      // dello user estratto sia DOCTOR
      for (Role r : doctor.getRoles()) {
        if (r.getName().equals("ROLE_DOCTOR")) {
          return doctor;
        } else throw new UsernameNotFoundException(("Dottore non trovato con questo username"));
      }
    }
    return doctor;
  }

  /**
   * Metodo usato durante la validazione dei dati della form di registrazione di un nuovo utente.
   *
   * @param username parametro per la ricerca. E' lo username con cui è stato registrato un utente.
   * @throws ValidationException viene lanciata se viene trova nel DB una corrispondenza allo username inserito
   *                             nella form.
   */
  public void findByUsernameForValidation(String username) throws ValidationException {
    if (userRepository.findUserByUsername(username) != null) {
      throw new ValidationException("Username is already used");
    }
  }

  /**
   * Metodo usato durante la validazione dei dati della form di registrazione di un nuovo utente.
   *
   * @param email parametro per la ricerca. E' la mail con cui è stato registrato un utente precedente.
   * @throws ValidationException viene lanciata se viene trova nel DB una corrispondenza alla mail inserita
   *                             nella form.
   */
  public void findEmailForValidation(String email) throws ValidationException {
    if (userRepository.findByEmail(email) != null) {
      throw new ValidationException("Email is already used");
    }
  }

  /**
   * Metodo usato durante la validazione dei dati della form di registrazione di un nuovo utente di ruolo ROLE_NURSE
   * o ROLE_ADMIN.
   *
   * @param name    parametro per la ricerca. E' il nome con cui è stato registrato un utente precedente.
   * @param surname parametro per la ricerca. E' il cognome con cui è stato registrato un utente precedente.
   * @throws ValidationException viene lanciata se viene trova nel DB una corrispondenza al nome E cognome (insieme) inseriti
   *                             *                             nella form.
   */
  public void findByNameAndSurnameForValidation(String name, String surname) throws ValidationException {
    if (userRepository.findByNameAndSurname(name, surname) != null) {
      throw new ValidationException("User is already in the Database");
    }
  }


}