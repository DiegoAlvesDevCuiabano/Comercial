package com.controle_comercial.service;

import com.controle_comercial.model.entity.Cliente;
import com.controle_comercial.model.entity.Evento;
import com.controle_comercial.model.entity.Local;
import com.controle_comercial.model.entity.Servico;
import com.controle_comercial.repository.EventoRepository;
import com.controle_comercial.repository.EventoServicoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventoService {

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
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        Local local = localService.buscarPorId(localId)
                .orElseThrow(() -> new RuntimeException("Local não encontrado"));
        evento.setCliente(cliente);
        evento.setLocal(local);

        Double descontoValor = params.containsKey("descontoValor")
                ? Double.parseDouble(params.get("descontoValor"))
                : 0.0;
        evento.setDescontoValor(descontoValor);

        if (evento.getValorTotal() != null && evento.getValorTotal() > 0) {
            double perc = (descontoValor / (evento.getValorTotal() + descontoValor)) * 100;
            evento.setDescontoPercentual(perc);
        } else {
            evento.setDescontoPercentual(0.0);
        }

        evento.getServicos().clear();
        String[] servicosIds = params.getOrDefault("servicosSelecionados", "").split(",");
        for (String s : servicosIds) {
            if (s.isEmpty()) continue;
            String[] parts = s.split(":");
            Integer servicoId = Integer.parseInt(parts[0]);
            Integer quantidade = Integer.parseInt(parts[1]);

            Servico servico = servicoService.buscarPorId(servicoId)
                    .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));

            evento.adicionarServico(servico, quantidade);
        }

        return salvar(evento);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> buscarEventoParaEdicao(Integer id) {
        return buscarPorId(id)
                .map(evento -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("idEvento", evento.getIdEvento());
                    response.put("titulo", evento.getTitulo());
                    response.put("dataInicio", evento.getDataInicio().toString());
                    response.put("dataFim", evento.getDataFim().toString());
                    response.put("horaInicio", evento.getHoraInicio().toString());
                    response.put("horaFim", evento.getHoraFim().toString());
                    response.put("valorTotal", evento.getValorTotal());
                    response.put("observacoes", evento.getObservacoes());
                    response.put("descontoValor", evento.getDescontoValor());
                    response.put("descontoPercentual", evento.getDescontoPercentual());

                    if (evento.getCliente() != null) {
                        Map<String, Object> clienteMap = new HashMap<>();
                        clienteMap.put("idCliente", evento.getCliente().getIdCliente());
                        clienteMap.put("nome", evento.getCliente().getNome());
                        response.put("cliente", clienteMap);
                    }

                    if (evento.getLocal() != null) {
                        Map<String, Object> localMap = new HashMap<>();
                        localMap.put("idLocal", evento.getLocal().getIdLocal());
                        localMap.put("nome", evento.getLocal().getNome());
                        response.put("local", localMap);
                    }

                    if (evento.getServicos() != null && !evento.getServicos().isEmpty()) {
                        List<Map<String, Object>> servicosList = evento.getServicos().stream()
                                .map(es -> {
                                    Map<String, Object> servicoMap = new HashMap<>();
                                    servicoMap.put("quantidade", es.getQuantidade());
                                    if (es.getServico() != null) {
                                        Map<String, Object> s = new HashMap<>();
                                        s.put("idServico", es.getServico().getIdServico());
                                        s.put("nome", es.getServico().getNome());
                                        s.put("precoUnitario", es.getServico().getPrecoUnitario());
                                        servicoMap.put("servico", s);
                                    }
                                    return servicoMap;
                                })
                                .collect(Collectors.toList());
                        response.put("servicos", servicosList);
                    }

                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> listarEventosPorPeriodo(String inicio, String fim) {
        LocalDate dataInicio = LocalDate.parse(inicio);
        LocalDate dataFim = LocalDate.parse(fim);

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
