package com.controle_comercial.service;

import com.controle_comercial.exception.ClienteNotFoundException;
import com.controle_comercial.exception.EntityNotFoundException;
import com.controle_comercial.exception.LocalNotFoundException;
import com.controle_comercial.exception.ServicoNotFoundException;
import com.controle_comercial.exception.ValidationException;
import com.controle_comercial.model.entity.*;
import com.controle_comercial.repository.OrcamentoRepository;
import com.controle_comercial.repository.OrcamentoServicoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OrcamentoService {

    private static final Logger logger = LoggerFactory.getLogger(OrcamentoService.class);

    private final OrcamentoRepository repository;
    private final OrcamentoServicoRepository orcamentoServicoRepository;
    private final ClienteService clienteService;
    private final LocalService localService;
    private final ServicoService servicoService;
    private final EventoService eventoService;

    public OrcamentoService(OrcamentoRepository repository,
                            OrcamentoServicoRepository orcamentoServicoRepository,
                            ClienteService clienteService,
                            LocalService localService,
                            ServicoService servicoService,
                            EventoService eventoService) {
        this.repository = repository;
        this.orcamentoServicoRepository = orcamentoServicoRepository;
        this.clienteService = clienteService;
        this.localService = localService;
        this.servicoService = servicoService;
        this.eventoService = eventoService;
    }

    @Transactional(readOnly = true)
    public List<Orcamento> listarTodos() {
        return repository.findAllByOrderByDataCriacaoDesc();
    }

    @Transactional(readOnly = true)
    public List<Orcamento> listarPorStatus(StatusOrcamento status) {
        return repository.findByStatusOrderByDataCriacaoDesc(status);
    }

    @Transactional(readOnly = true)
    public Optional<Orcamento> buscarPorId(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    public Orcamento salvarOrcamentoComServicos(Orcamento orcamento, Integer clienteId, Integer localId, Map<String, String> params) {
        Cliente cliente = clienteService.buscarPorId(clienteId)
                .orElseThrow(() -> new ClienteNotFoundException(clienteId));
        Local local = localService.buscarPorId(localId)
                .orElseThrow(() -> new LocalNotFoundException(localId));

        orcamento.setCliente(cliente);
        orcamento.setLocal(local);

        // Gerar número do orçamento se novo
        if (orcamento.getIdOrcamento() == null) {
            orcamento.setNumeroOrcamento(gerarNumeroOrcamento());
            orcamento.setDataCriacao(LocalDateTime.now());
        }

        // Calcular desconto
        BigDecimal descontoValor = params.containsKey("descontoValor")
                ? new BigDecimal(params.get("descontoValor"))
                : BigDecimal.ZERO;
        orcamento.setDescontoValor(descontoValor);

        if (orcamento.getValorTotal() != null && orcamento.getValorTotal().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal base = orcamento.getValorTotal().add(descontoValor);
            BigDecimal perc = descontoValor.multiply(new BigDecimal("100")).divide(base, 2, RoundingMode.HALF_UP);
            orcamento.setDescontoPercentual(perc);
        } else {
            orcamento.setDescontoPercentual(BigDecimal.ZERO);
        }

        // Processar serviços
        orcamento.getServicos().clear();
        String servicosSelecionados = params.getOrDefault("servicosSelecionados", "");
        String[] servicosIds = servicosSelecionados.split(",");

        for (String s : servicosIds) {
            String servicoStr = s.trim();
            if (servicoStr.isEmpty()) continue;

            String[] parts = servicoStr.split(":");
            if (parts.length != 2) {
                throw new ValidationException("Formato de serviço inválido: '" + servicoStr + "'");
            }

            Integer servicoId = Integer.parseInt(parts[0].trim());
            Integer quantidade = Integer.parseInt(parts[1].trim());

            if (quantidade <= 0) {
                throw new ValidationException("Quantidade deve ser maior que zero");
            }

            Servico servico = servicoService.buscarPorId(servicoId)
                    .orElseThrow(() -> new ServicoNotFoundException(servicoId));

            orcamento.adicionarServico(servico, quantidade);
        }

        return repository.save(orcamento);
    }

    @Transactional
    public void deletar(Integer id) {
        orcamentoServicoRepository.deleteByOrcamentoId(id);
        repository.deleteById(id);
    }

    @Transactional
    public Orcamento atualizarStatus(Integer id, StatusOrcamento novoStatus) {
        Orcamento orcamento = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Orçamento", id));

        if (orcamento.getStatus() == StatusOrcamento.REJEITADO || orcamento.getStatus() == StatusOrcamento.EXPIRADO) {
            throw new ValidationException("Não é possível alterar status de orçamento " + orcamento.getStatus().getDescricao());
        }

        orcamento.setStatus(novoStatus);

        if (novoStatus == StatusOrcamento.ENVIADO) {
            orcamento.setDataEnvio(LocalDateTime.now());
        } else if (novoStatus == StatusOrcamento.APROVADO || novoStatus == StatusOrcamento.REJEITADO) {
            orcamento.setDataResposta(LocalDateTime.now());
        }

        return repository.save(orcamento);
    }

    @Transactional
    public Evento converterParaEvento(Integer orcamentoId) {
        Orcamento orcamento = repository.findById(orcamentoId)
                .orElseThrow(() -> new EntityNotFoundException("Orçamento", orcamentoId));

        if (orcamento.getStatus() != StatusOrcamento.APROVADO) {
            throw new ValidationException("Apenas orçamentos aprovados podem ser convertidos em evento");
        }

        Evento evento = new Evento();
        evento.setTitulo("Evento - " + orcamento.getNumeroOrcamento());
        evento.setDataInicio(orcamento.getDataInicioEvento());
        evento.setDataFim(orcamento.getDataFimEvento());
        evento.setHoraInicio(orcamento.getHoraInicio());
        evento.setHoraFim(orcamento.getHoraFim());
        evento.setCliente(orcamento.getCliente());
        evento.setLocal(orcamento.getLocal());
        evento.setValorTotal(orcamento.getValorTotal());
        evento.setDescontoValor(orcamento.getDescontoValor());
        evento.setDescontoPercentual(orcamento.getDescontoPercentual());
        evento.setObservacoes(orcamento.getObservacoes());
        evento.setStatus(StatusEvento.CONFIRMADO);

        Evento eventoSalvo = eventoService.salvar(evento);

        // Copiar serviços
        for (OrcamentoServico os : orcamento.getServicos()) {
            eventoSalvo.adicionarServico(os.getServico(), os.getQuantidade());
        }

        return eventoService.salvar(eventoSalvo);
    }

    private String gerarNumeroOrcamento() {
        int ano = Year.now().getValue();
        int proximoId = repository.findMaxId() + 1;
        return String.format("ORC-%d-%03d", ano, proximoId);
    }
}
