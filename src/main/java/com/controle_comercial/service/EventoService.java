package com.controle_comercial.service;

import com.controle_comercial.model.entity.*;
import com.controle_comercial.repository.EventoRepository;
import com.controle_comercial.repository.EventoServicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EventoService {

    private final EventoRepository repository;
    private final EventoServicoRepository eventoServicoRepository;

    public EventoService(EventoRepository repository,
                         EventoServicoRepository eventoServicoRepository) {
        this.repository = repository;
        this.eventoServicoRepository = eventoServicoRepository;
    }

    @Transactional(readOnly = true)
    public List<Evento> listarTodos() {
        return repository.findAllByOrderByDataEventoAsc();
    }

    @Transactional
    public Evento salvar(Evento evento) {
        // Para eventos existentes, o Hibernate irá gerenciar a coleção automaticamente
        return repository.save(evento);
    }


    @Transactional(readOnly = true)
    public Optional<Evento> buscarPorId(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    public void deletar(Integer id) {
        // Primeiro remove as associações com serviços
        eventoServicoRepository.deleteByEventoId(id);
        // Depois remove o evento
        repository.deleteById(id);
    }
}