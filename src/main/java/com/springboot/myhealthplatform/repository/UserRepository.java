package com.springboot.myhealthplatform.repository;

import com.springboot.myhealthplatform.bean.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 * Metodi utili al recupero e salvataggio dei dati riferiti alla classe User nel database.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  User findUserByUsername(String username);
  List<User> findUsersByRolesId(Integer roleId);

  User findByEmail(String email);
  User findByNameAndSurname(String name, String surname);


}
