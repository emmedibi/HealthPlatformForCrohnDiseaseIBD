package com.springboot.myhealthplatform;

import com.springboot.myhealthplatform.login.CustomUserDetailsService;
import com.springboot.myhealthplatform.repository.RoleRepository;
import com.springboot.myhealthplatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;
import org.springframework.web.filter.HiddenHttpMethodFilter;

import java.util.Arrays;

import static org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter.Directive.*;

@Configuration
@EnableWebSecurity
public class SecSecurityConfig {

  // we also have to define a password encoder
  @Autowired
  UserRepository userRepository;
  RoleRepository roleRepository;

  // Prova per logout con Clear-Site-Data Header Logout
  // Importato ClearSiteDataHeaderWriter e le sue variabili
  private static final ClearSiteDataHeaderWriter.Directive[] SOURCE =
          {ALL, CACHE, COOKIES, STORAGE, EXECUTION_CONTEXTS};

  /**
   * Implementazione dell'oggetto SecurityFilterChain che si occupa di verificare le autorizzazioni
   * dell'utente loggato. Nel caso in cui l'utente cerchi di fare una richiesta ad un indirizzo
   * a cui non può accedere, verrà restituito un errore di tipo 403 FORBIDDEN.
   * Le autorizzazioni vengono date in base al ruolo assegnato in fase di registrazione.
   * @param http richiesta http da verificare in base alle autorizzazioni dell'utente loggato.
   * @return l'accesso all'indirizzo richiesto.
   * @throws Exception lancio dell'eccezione di tipo 403 FORBIDDEN.
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests()
        .requestMatchers("/admin/**").hasRole("ADMIN")
        .requestMatchers("/doctor/**").hasRole("DOCTOR")
        .requestMatchers("/patient/**").hasRole("PATIENT")
        .requestMatchers("/diary/**").hasRole("PATIENT")
        .requestMatchers("/message/**").hasRole("PATIENT")
        .requestMatchers("/agenda/**").hasRole("PATIENT")
        .requestMatchers("/nurse/**").hasRole("NURSE")
        .requestMatchers("/login/*").permitAll()
        .anyRequest().authenticated()
        .and()
        .formLogin()
        .loginPage("/login")// custom login page
        .loginProcessingUrl("/login")           // the URL to submit the username and password to
        .defaultSuccessUrl("/index")  // the landing page after a successful login
        .permitAll()
        .and()
        .logout()
        .logoutUrl("/perform_logout")
        .logoutSuccessUrl("/login")
        .invalidateHttpSession(true)
        .addLogoutHandler(new HeaderWriterLogoutHandler(
                    new ClearSiteDataHeaderWriter(SOURCE)))
        .deleteCookies("JSESSIONID");

//                .logoutSuccessHandler(logoutSuccessHandler());
    return http.build();
  }

  /**
   * Implementazione della classe DaoAuthenticationProvider che permette di ottenere le informazioni
   * dell'utente e gestirle per l'autenticazione dell'utente nell'applicazione.
   * @return oggetto di tipo DaoAuthenticationProvider.
   */
  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(new CustomUserDetailsService(userRepository, roleRepository));
    authProvider.setPasswordEncoder(new BCryptPasswordEncoder());
    return authProvider;
  }

  /**
   * Implementazione della classe BCryptPasswordEncoder che permette la criptazione
   * della password.
   * @return
   */
  @Bean
  public BCryptPasswordEncoder passwordEncoder() {

    return new BCryptPasswordEncoder();
  }


  /**
   * Permette la gestione dei metodi PUT e DELETE.
   * @return
   */
  @Bean
  public FilterRegistrationBean hiddenHttpMethodFilter() {
    FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new HiddenHttpMethodFilter());
    filterRegistrationBean.setUrlPatterns(Arrays.asList("/*"));
    return filterRegistrationBean;
  }

}