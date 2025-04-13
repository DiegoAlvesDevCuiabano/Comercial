package com.controle_comercial.repository;

import com.controle_comercial.model.entity.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, Long> {
    // Busca todos os eventos para uma data específica
    List<Evento> findByData(LocalDate data);
}

