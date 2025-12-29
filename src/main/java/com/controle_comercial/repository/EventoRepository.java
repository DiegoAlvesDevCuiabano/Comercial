package com.controle_comercial.repository;

import com.controle_comercial.model.entity.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, Integer> {
    List<Evento> findAllByOrderByDataInicioAsc();

    @Query("SELECT e FROM Evento e WHERE e.dataInicio >= :inicio AND e.dataFim <= :fim ORDER BY e.dataInicio ASC")
    List<Evento> findByPeriodo(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);
}
