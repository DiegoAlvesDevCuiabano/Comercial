package com.controle_comercial.service;

import com.controle_comercial.model.entity.Servico;
import com.controle_comercial.repository.ServicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ServicoService {

    private final ServicoRepository repository;

    public ServicoService(ServicoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Servico> listarTodos() {
        return repository.findAllByOrderByNomeAsc();
    }

    @Transactional
    public Servico salvar(Servico servico) {
        if (servico.getIdServico() != null) {
            return repository.findById(servico.getIdServico())
                    .map(existente -> {
                        existente.setNome(servico.getNome());
                        existente.setDescricao(servico.getDescricao());
                        existente.setPrecoUnitario(servico.getPrecoUnitario());
                        return repository.save(existente);
                    })
                    .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));
        }
        return repository.save(servico);
    }

    @Transactional(readOnly = true)
    public Optional<Servico> buscarPorId(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    public void deletar(Integer id) {
        repository.deleteById(id);
    }
}
