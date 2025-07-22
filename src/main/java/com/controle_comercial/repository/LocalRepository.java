package com.controle_comercial.repository;

import com.controle_comercial.model.entity.Local;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocalRepository extends JpaRepository<Local, Integer> {
    List<Local> findAllByOrderByNomeAsc();
}
