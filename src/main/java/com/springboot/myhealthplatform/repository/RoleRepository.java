package com.springboot.myhealthplatform.repository;

import com.springboot.myhealthplatform.bean.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
/**
 * Metodi utili al recupero e salvataggio dei dati riferiti alla classe Role nel database.
 */
public interface RoleRepository extends JpaRepository<Role, Integer> {

    Role findByName(String name);
    List<Role> findRolesByUsersId(Integer userId);

}
