package com.controle_comercial.repository;

import com.controle_comercial.model.entity.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, Integer> {
    List<Evento> findAllByOrderByDataEventoAsc();
}
