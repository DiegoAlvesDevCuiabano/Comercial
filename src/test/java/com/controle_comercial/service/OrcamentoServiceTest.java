package com.controle_comercial.service;

import com.controle_comercial.exception.EntityNotFoundException;
import com.controle_comercial.exception.ValidationException;
import com.controle_comercial.model.entity.*;
import com.controle_comercial.repository.OrcamentoRepository;
import com.controle_comercial.repository.OrcamentoServicoRepository;
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
class OrcamentoServiceTest {

    @Mock
    private OrcamentoRepository repository;

    @Mock
    private OrcamentoServicoRepository orcamentoServicoRepository;

    @Mock
    private ClienteService clienteService;

    @Mock
    private LocalService localService;

    @Mock
    private ServicoService servicoService;

    @Mock
    private EventoService eventoService;

    @InjectMocks
    private OrcamentoService orcamentoService;

    private Orcamento orcamento;
    private Cliente cliente;
    private Local local;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setIdCliente(1);
        cliente.setNome("Empresa ABC");

        local = new Local();
        local.setIdLocal(1);
        local.setNome("Auditório");

        orcamento = new Orcamento();
        orcamento.setIdOrcamento(1);
        orcamento.setNumeroOrcamento("ORC-2026-001");
        orcamento.setCliente(cliente);
        orcamento.setLocal(local);
        orcamento.setDataInicioEvento(LocalDate.of(2026, 5, 10));
        orcamento.setDataFimEvento(LocalDate.of(2026, 5, 10));
        orcamento.setHoraInicio(LocalTime.of(9, 0));
        orcamento.setHoraFim(LocalTime.of(17, 0));
        orcamento.setValorTotal(new BigDecimal("1000.00"));
        orcamento.setStatus(StatusOrcamento.RASCUNHO);
    }

    @Test
    void listarTodos_deveRetornarOrcamentos() {
        when(repository.findAllByOrderByDataCriacaoDesc()).thenReturn(Arrays.asList(orcamento));

        List<Orcamento> resultado = orcamentoService.listarTodos();

        assertEquals(1, resultado.size());
        assertEquals("ORC-2026-001", resultado.get(0).getNumeroOrcamento());
    }

    @Test
    void listarPorStatus_deveRetornarFiltrado() {
        when(repository.findByStatusOrderByDataCriacaoDesc(StatusOrcamento.RASCUNHO))
                .thenReturn(Arrays.asList(orcamento));

        List<Orcamento> resultado = orcamentoService.listarPorStatus(StatusOrcamento.RASCUNHO);

        assertEquals(1, resultado.size());
    }

    @Test
    void atualizarStatus_rascunhoParaEnviado_deveAtualizar() {
        when(repository.findById(1)).thenReturn(Optional.of(orcamento));
        when(repository.save(any(Orcamento.class))).thenReturn(orcamento);

        Orcamento resultado = orcamentoService.atualizarStatus(1, StatusOrcamento.ENVIADO);

        assertEquals(StatusOrcamento.ENVIADO, resultado.getStatus());
        assertNotNull(resultado.getDataEnvio());
    }

    @Test
    void atualizarStatus_rejeitado_deveLancarExcecao() {
        orcamento.setStatus(StatusOrcamento.REJEITADO);
        when(repository.findById(1)).thenReturn(Optional.of(orcamento));

        assertThrows(ValidationException.class,
                () -> orcamentoService.atualizarStatus(1, StatusOrcamento.ENVIADO));
    }

    @Test
    void atualizarStatus_inexistente_deveLancarExcecao() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> orcamentoService.atualizarStatus(999, StatusOrcamento.ENVIADO));
    }

    @Test
    void converterParaEvento_aprovado_deveCriarEvento() {
        orcamento.setStatus(StatusOrcamento.APROVADO);
        when(repository.findById(1)).thenReturn(Optional.of(orcamento));

        Evento eventoMock = new Evento();
        eventoMock.setIdEvento(10);
        eventoMock.setTitulo("Evento - ORC-2026-001");
        when(eventoService.salvar(any(Evento.class))).thenReturn(eventoMock);

        Evento resultado = orcamentoService.converterParaEvento(1);

        assertNotNull(resultado);
        verify(eventoService, times(2)).salvar(any(Evento.class));
    }

    @Test
    void converterParaEvento_naoAprovado_deveLancarExcecao() {
        orcamento.setStatus(StatusOrcamento.RASCUNHO);
        when(repository.findById(1)).thenReturn(Optional.of(orcamento));

        assertThrows(ValidationException.class,
                () -> orcamentoService.converterParaEvento(1));
    }

    @Test
    void converterParaEvento_inexistente_deveLancarExcecao() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> orcamentoService.converterParaEvento(999));
    }

    @Test
    void deletar_deveChamarRepositories() {
        orcamentoService.deletar(1);

        verify(orcamentoServicoRepository).deleteByOrcamentoId(1);
        verify(repository).deleteById(1);
    }
}
