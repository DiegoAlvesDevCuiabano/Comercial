package com.controle_comercial.service;

import com.controle_comercial.model.entity.Local;
import com.controle_comercial.repository.LocalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class LocalService {

    private final LocalRepository repository;

    public LocalService(LocalRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Local> listarTodos() {
        return repository.findAllByOrderByNomeAsc();
    }

    @Transactional
    public Local salvar(Local local) {
        if (local.getIdLocal() != null) {
            return repository.findById(local.getIdLocal())
                    .map(existente -> {
                        existente.setNome(local.getNome());
                        existente.setTipo(local.getTipo());
                        existente.setCapacidade(local.getCapacidade());
                        return repository.save(existente);
                    })
                    .orElseThrow(() -> new RuntimeException("Local não encontrado"));
        }
        return repository.save(local);
    }

    @Transactional(readOnly = true)
    public Optional<Local> buscarPorId(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    public void deletar(Integer id) {
        repository.deleteById(id);
    }
}
