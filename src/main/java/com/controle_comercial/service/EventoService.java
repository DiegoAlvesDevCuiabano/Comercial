package com.controle_comercial.service;

import com.controle_comercial.model.entity.Evento;
import com.controle_comercial.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    public List<Evento> listarTodos() {
        return eventoRepository.findAll();
    }

    public List<Evento> listarPorData(LocalDate data) {
        return eventoRepository.findByDataEvento(data);
    }

    public Evento buscarPorId(Integer id) {
        return eventoRepository.findByIdEvento(id).get(0);
    }

}

