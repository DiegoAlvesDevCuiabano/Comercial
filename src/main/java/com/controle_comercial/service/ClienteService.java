package com.controle_comercial.service;

import com.controle_comercial.model.entity.Cliente;
import com.controle_comercial.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() {
        return clienteRepository.findAllByOrderByNomeAsc();
    }

    @Transactional
    public void deletar(Integer id) {
        clienteRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorId(Integer id) {
        return clienteRepository.findById(id);
    }

    @Transactional
    public Cliente salvar(Cliente cliente) {
        if (cliente.getIdCliente() != null) {
            // Atualização
            return clienteRepository.findById(cliente.getIdCliente())
                    .map(existente -> {
                        existente.setNome(cliente.getNome());
                        existente.setDocumento(cliente.getDocumento());
                        existente.setTelefone(cliente.getTelefone());
                        existente.setEmail(cliente.getEmail());
                        return clienteRepository.save(existente);
                    })
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        }
        // Criação
        return clienteRepository.save(cliente);
    }

    @Transactional(readOnly = true)
    public boolean existePorId(Integer id) {
        return clienteRepository.existsById(id);
    }
}