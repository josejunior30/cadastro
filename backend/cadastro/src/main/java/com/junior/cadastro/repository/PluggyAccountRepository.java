package com.junior.cadastro.repository;



import org.springframework.data.jpa.repository.JpaRepository;

import com.junior.cadastro.entities.PluggyAccount;


public interface PluggyAccountRepository extends JpaRepository<PluggyAccount, Long> {

  
}