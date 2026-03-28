package com.controle_comercial.service;

import com.controle_comercial.exception.ClienteNotFoundException;
import com.controle_comercial.exception.LocalNotFoundException;
import com.controle_comercial.exception.ServicoNotFoundException;
import com.controle_comercial.exception.ValidationException;
import com.controle_comercial.model.dto.EventoEditDTO;
import com.controle_comercial.model.entity.Cliente;
import com.controle_comercial.model.entity.Evento;
import com.controle_comercial.model.entity.Local;
import com.controle_comercial.model.entity.Servico;
import com.controle_comercial.model.entity.StatusEvento;
import com.controle_comercial.repository.EventoRepository;
import com.controle_comercial.repository.EventoServicoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventoService {

    private static final Logger logger = LoggerFactory.getLogger(EventoService.class);

    private final EventoRepository repository;
    private final EventoServicoRepository eventoServicoRepository;
    private final ClienteService clienteService;
    private final LocalService localService;
    private final ServicoService servicoService;

    public EventoService(EventoRepository repository,
                         EventoServicoRepository eventoServicoRepository,
                         ClienteService clienteService,
                         LocalService localService,
                         ServicoService servicoService) {
        this.repository = repository;
        this.eventoServicoRepository = eventoServicoRepository;
        this.clienteService = clienteService;
        this.localService = localService;
        this.servicoService = servicoService;
    }

    @Transactional(readOnly = true)
    public List<Evento> listarTodos() {
        return repository.findAllByOrderByDataInicioAsc();
    }

    @Transactional(readOnly = true)
    public List<Evento> listarComFiltros(LocalDate dataInicio, LocalDate dataFim,
                                         Integer clienteId, Integer localId,
                                         StatusEvento status) {
        return repository.findByFiltros(dataInicio, dataFim, clienteId, localId, status);
    }

    @Transactional
    public Evento salvar(Evento evento) {
        return repository.save(evento);
    }

    @Transactional
    public void deletar(Integer id) {
        eventoServicoRepository.deleteByEventoId(id);
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Evento> buscarPorId(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    public Evento salvarEventoComServicos(Evento evento, Integer clienteId, Integer localId, Map<String, String> params) {

        Cliente cliente = clienteService.buscarPorId(clienteId)
                .orElseThrow(() -> new ClienteNotFoundException(clienteId));
        Local local = localService.buscarPorId(localId)
                .orElseThrow(() -> new LocalNotFoundException(localId));
        evento.setCliente(cliente);
        evento.setLocal(local);

        BigDecimal descontoValor = params.containsKey("descontoValor")
                ? new BigDecimal(params.get("descontoValor"))
                : BigDecimal.ZERO;
        evento.setDescontoValor(descontoValor);

        if (evento.getValorTotal() != null && evento.getValorTotal().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal base = evento.getValorTotal().add(descontoValor);
            BigDecimal perc = descontoValor.multiply(new BigDecimal("100")).divide(base, 2, RoundingMode.HALF_UP);
            evento.setDescontoPercentual(perc);
        } else {
            evento.setDescontoPercentual(BigDecimal.ZERO);
        }

        evento.getServicos().clear();
        String[] servicosIds = params.getOrDefault("servicosSelecionados", "").split(",");

        for (String s : servicosIds) {
            String servicoStr = s.trim();
            if (servicoStr.isEmpty()) continue;

            String[] parts = servicoStr.split(":");
            if (parts.length != 2) {
                throw new ValidationException(
                    "Formato de serviço inválido: '" + servicoStr + "'. Esperado: 'id:quantidade'"
                );
            }

            Integer servicoId;
            Integer quantidade;
            try {
                servicoId = Integer.parseInt(parts[0].trim());
                quantidade = Integer.parseInt(parts[1].trim());
            } catch (NumberFormatException e) {
                throw new ValidationException(
                    "ID ou quantidade inválidos no serviço: '" + servicoStr + "'", e
                );
            }

            if (quantidade <= 0) {
                throw new ValidationException("Quantidade deve ser maior que zero. Serviço ID: " + servicoId);
            }

            Servico servico = servicoService.buscarPorId(servicoId)
                    .orElseThrow(() -> new ServicoNotFoundException(servicoId));

            evento.adicionarServico(servico, quantidade);
        }

        return salvar(evento);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<EventoEditDTO> buscarEventoParaEdicao(Integer id) {
        return buscarPorId(id)
                .map(evento -> ResponseEntity.ok(EventoEditDTO.fromEntity(evento)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> listarEventosPorPeriodo(String inicio, String fim) {
        LocalDate dataInicio;
        LocalDate dataFim;

        try {
            dataInicio = LocalDate.parse(inicio);
            dataFim = LocalDate.parse(fim);
        } catch (DateTimeParseException e) {
            throw new ValidationException("Formato de data inválido. Use yyyy-MM-dd", e);
        }

        if (dataFim.isBefore(dataInicio)) {
            throw new ValidationException("Data fim não pode ser anterior a data início");
        }

        return repository.findByPeriodo(dataInicio, dataFim).stream()
                .map(e -> {
                    Map<String, Object> mapa = new HashMap<>();
                    mapa.put("idEvento", e.getIdEvento());
                    mapa.put("titulo", e.getTitulo());
                    mapa.put("dataInicio", e.getDataInicio().toString());
                    mapa.put("dataFim", e.getDataFim().toString());
                    return mapa;
                })
                .collect(Collectors.toList());
    }

}
