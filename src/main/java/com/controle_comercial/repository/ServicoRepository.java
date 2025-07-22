package com.controle_comercial.repository;

import com.controle_comercial.model.entity.Servico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServicoRepository extends JpaRepository<Servico, Integer> {
    List<Servico> findAllByOrderByNomeAsc();
}
