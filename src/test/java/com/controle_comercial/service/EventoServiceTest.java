package com.controle_comercial.service;

import com.controle_comercial.exception.ClienteNotFoundException;
import com.controle_comercial.exception.ValidationException;
import com.controle_comercial.model.entity.*;
import com.controle_comercial.repository.EventoRepository;
import com.controle_comercial.repository.EventoServicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventoServiceTest {

    @Mock
    private EventoRepository repository;

    @Mock
    private EventoServicoRepository eventoServicoRepository;

    @Mock
    private ClienteService clienteService;

    @Mock
    private LocalService localService;

    @Mock
    private ServicoService servicoService;

    @InjectMocks
    private EventoService eventoService;

    private Evento evento;
    private Cliente cliente;
    private Local local;
    private Servico servico;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setIdCliente(1);
        cliente.setNome("Empresa ABC");

        local = new Local();
        local.setIdLocal(1);
        local.setNome("Auditório");

        servico = new Servico();
        servico.setIdServico(1);
        servico.setNome("Coffee Break");
        servico.setPrecoUnitario(new BigDecimal("50.00"));

        evento = new Evento();
        evento.setIdEvento(1);
        evento.setTitulo("Workshop Java");
        evento.setDataInicio(LocalDate.of(2026, 4, 10));
        evento.setDataFim(LocalDate.of(2026, 4, 10));
        evento.setHoraInicio(LocalTime.of(9, 0));
        evento.setHoraFim(LocalTime.of(17, 0));
        evento.setCliente(cliente);
        evento.setLocal(local);
        evento.setValorTotal(new BigDecimal("500.00"));
        evento.setStatus(StatusEvento.PLANEJAMENTO);
    }

    @Test
    void listarTodos_deveRetornarEventos() {
        when(repository.findAllByOrderByDataInicioAsc()).thenReturn(Arrays.asList(evento));

        List<Evento> resultado = eventoService.listarTodos();

        assertEquals(1, resultado.size());
        assertEquals("Workshop Java", resultado.get(0).getTitulo());
    }

    @Test
    void buscarPorId_existente() {
        when(repository.findById(1)).thenReturn(Optional.of(evento));

        Optional<Evento> resultado = eventoService.buscarPorId(1);

        assertTrue(resultado.isPresent());
    }

    @Test
    void salvarEventoComServicos_clienteInexistente_deveLancarExcecao() {
        when(clienteService.buscarPorId(999)).thenReturn(Optional.empty());

        Map<String, String> params = new HashMap<>();

        assertThrows(ClienteNotFoundException.class,
                () -> eventoService.salvarEventoComServicos(evento, 999, 1, params));
    }

    @Test
    void salvarEventoComServicos_comDesconto_deveCalcularPercentual() {
        when(clienteService.buscarPorId(1)).thenReturn(Optional.of(cliente));
        when(localService.buscarPorId(1)).thenReturn(Optional.of(local));
        when(repository.save(any(Evento.class))).thenReturn(evento);

        evento.setValorTotal(new BigDecimal("900.00"));

        Map<String, String> params = new HashMap<>();
        params.put("descontoValor", "100");
        params.put("servicosSelecionados", "");

        eventoService.salvarEventoComServicos(evento, 1, 1, params);

        assertNotNull(evento.getDescontoPercentual());
        assertTrue(evento.getDescontoPercentual().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void salvarEventoComServicos_formatoServicoInvalido_deveLancarExcecao() {
        when(clienteService.buscarPorId(1)).thenReturn(Optional.of(cliente));
        when(localService.buscarPorId(1)).thenReturn(Optional.of(local));

        Map<String, String> params = new HashMap<>();
        params.put("servicosSelecionados", "invalido");

        assertThrows(ValidationException.class,
                () -> eventoService.salvarEventoComServicos(evento, 1, 1, params));
    }

    @Test
    void salvarEventoComServicos_quantidadeZero_deveLancarExcecao() {
        when(clienteService.buscarPorId(1)).thenReturn(Optional.of(cliente));
        when(localService.buscarPorId(1)).thenReturn(Optional.of(local));

        Map<String, String> params = new HashMap<>();
        params.put("servicosSelecionados", "1:0");

        assertThrows(ValidationException.class,
                () -> eventoService.salvarEventoComServicos(evento, 1, 1, params));
    }

    @Test
    void listarEventosPorPeriodo_dataInvalida_deveLancarExcecao() {
        assertThrows(ValidationException.class,
                () -> eventoService.listarEventosPorPeriodo("data-invalida", "2026-04-30"));
    }

    @Test
    void listarEventosPorPeriodo_fimAnteInicio_deveLancarExcecao() {
        assertThrows(ValidationException.class,
                () -> eventoService.listarEventosPorPeriodo("2026-04-30", "2026-04-01"));
    }

    @Test
    void listarEventosPorPeriodo_valido_deveRetornarLista() {
        when(repository.findByPeriodo(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Arrays.asList(evento));

        List<Map<String, Object>> resultado = eventoService.listarEventosPorPeriodo("2026-04-01", "2026-04-30");

        assertEquals(1, resultado.size());
        assertEquals("Workshop Java", resultado.get(0).get("titulo"));
    }

    @Test
    void deletar_deveChamarRepositories() {
        eventoService.deletar(1);

        verify(eventoServicoRepository).deleteByEventoId(1);
        verify(repository).deleteById(1);
    }
}
