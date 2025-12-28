package com.controle_comercial.repository;

import com.controle_comercial.model.entity.EventoServico;
import com.controle_comercial.model.entity.EventoServicoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface EventoServicoRepository extends JpaRepository<EventoServico, EventoServicoId> {

    @Transactional
    @Modifying
    @Query("DELETE FROM EventoServico es WHERE es.evento.idEvento = :eventoId")
    void deleteByEventoId(Integer eventoId);
}