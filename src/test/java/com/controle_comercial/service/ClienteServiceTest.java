package com.controle_comercial.service;

import com.controle_comercial.exception.ClienteNotFoundException;
import com.controle_comercial.model.entity.Cliente;
import com.controle_comercial.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setIdCliente(1);
        cliente.setNome("Empresa ABC");
        cliente.setEmail("contato@abc.com");
        cliente.setTelefone("(65) 99999-0000");
        cliente.setDocumento("12345678000100");
    }

    @Test
    void listarTodos_deveRetornarLista() {
        when(clienteRepository.findAllByOrderByNomeAsc()).thenReturn(Arrays.asList(cliente));

        List<Cliente> resultado = clienteService.listarTodos();

        assertEquals(1, resultado.size());
        assertEquals("Empresa ABC", resultado.get(0).getNome());
        verify(clienteRepository).findAllByOrderByNomeAsc();
    }

    @Test
    void buscarPorId_existente_deveRetornarCliente() {
        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));

        Optional<Cliente> resultado = clienteService.buscarPorId(1);

        assertTrue(resultado.isPresent());
        assertEquals("Empresa ABC", resultado.get().getNome());
    }

    @Test
    void buscarPorId_inexistente_deveRetornarVazio() {
        when(clienteRepository.findById(999)).thenReturn(Optional.empty());

        Optional<Cliente> resultado = clienteService.buscarPorId(999);

        assertFalse(resultado.isPresent());
    }

    @Test
    void salvar_novoCliente_deveSalvar() {
        Cliente novo = new Cliente();
        novo.setNome("Novo Cliente");
        when(clienteRepository.save(any(Cliente.class))).thenReturn(novo);

        Cliente resultado = clienteService.salvar(novo);

        assertNotNull(resultado);
        assertEquals("Novo Cliente", resultado.getNome());
        verify(clienteRepository).save(novo);
    }

    @Test
    void salvar_clienteExistente_deveAtualizar() {
        Cliente atualizado = new Cliente();
        atualizado.setIdCliente(1);
        atualizado.setNome("Empresa ABC Atualizada");

        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        Cliente resultado = clienteService.salvar(atualizado);

        assertNotNull(resultado);
        verify(clienteRepository).findById(1);
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    void salvar_clienteInexistente_deveLancarExcecao() {
        Cliente inexistente = new Cliente();
        inexistente.setIdCliente(999);
        inexistente.setNome("Inexistente");

        when(clienteRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ClienteNotFoundException.class, () -> clienteService.salvar(inexistente));
    }

    @Test
    void deletar_deveChamarRepository() {
        clienteService.deletar(1);
        verify(clienteRepository).deleteById(1);
    }
}
