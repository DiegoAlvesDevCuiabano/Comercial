package com.controle_comercial.repository;

import com.controle_comercial.model.entity.Orcamento;
import com.controle_comercial.model.entity.StatusOrcamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrcamentoRepository extends JpaRepository<Orcamento, Integer> {

    List<Orcamento> findAllByOrderByDataCriacaoDesc();

    List<Orcamento> findByStatusOrderByDataCriacaoDesc(StatusOrcamento status);

    @Query("SELECT COALESCE(MAX(o.idOrcamento), 0) FROM Orcamento o")
    Integer findMaxId();
}
