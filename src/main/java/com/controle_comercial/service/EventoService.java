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

    // Lista todos os eventos
    public List<Evento> listarTodos() {
        return eventoRepository.findAll();
    }

    // Retorna eventos de uma data específica
    public List<Evento> listarPorData(LocalDate data) {
        return eventoRepository.findByData(data);
    }

    // Salva ou atualiza um evento
    public Evento salvar(Evento evento) {
        return eventoRepository.save(evento);
    }

    // Busca um evento pelo ID
    public Evento buscarPorId(Long id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado!"));
    }

    // Exclui um evento
    public void excluir(Long id) {
        eventoRepository.deleteById(id);
    }
}

