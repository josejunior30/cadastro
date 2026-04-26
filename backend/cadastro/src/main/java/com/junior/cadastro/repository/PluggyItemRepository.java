package com.junior.cadastro.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.junior.cadastro.entities.PluggyItem;


public interface PluggyItemRepository extends JpaRepository<PluggyItem, Long> {

}