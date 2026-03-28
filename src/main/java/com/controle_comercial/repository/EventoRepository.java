package com.controle_comercial.repository;

import com.controle_comercial.model.entity.Evento;
import com.controle_comercial.model.entity.StatusEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, Integer> {
    List<Evento> findAllByOrderByDataInicioAsc();

    @Query("SELECT e FROM Evento e WHERE e.dataInicio >= :inicio AND e.dataFim <= :fim ORDER BY e.dataInicio ASC")
    List<Evento> findByPeriodo(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    @Query("SELECT e FROM Evento e WHERE " +
           "(:dataInicio IS NULL OR e.dataInicio >= :dataInicio) AND " +
           "(:dataFim IS NULL OR e.dataFim <= :dataFim) AND " +
           "(:clienteId IS NULL OR e.cliente.idCliente = :clienteId) AND " +
           "(:localId IS NULL OR e.local.idLocal = :localId) AND " +
           "(:status IS NULL OR e.status = :status) " +
           "ORDER BY e.dataInicio ASC")
    List<Evento> findByFiltros(
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim,
            @Param("clienteId") Integer clienteId,
            @Param("localId") Integer localId,
            @Param("status") StatusEvento status);
}
