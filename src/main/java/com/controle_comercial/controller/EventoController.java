package com.controle_comercial.controller;

import com.controle_comercial.model.entity.*;
import com.controle_comercial.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/eventos")
public class EventoController {

    private final EventoService eventoService;
    private final ServicoService servicoService;
    private final ClienteService clienteService;
    private final LocalService localService;

    public EventoController(EventoService eventoService,
                            ServicoService servicoService,
                            ClienteService clienteService,
                            LocalService localService) {
        this.eventoService = eventoService;
        this.servicoService = servicoService;
        this.clienteService = clienteService;
        this.localService = localService;
    }

    @GetMapping
    public String listarEventos(Model model) {
        model.addAttribute("eventos", eventoService.listarTodos());
        model.addAttribute("servicos", servicoService.listarTodos());
        model.addAttribute("clientes", clienteService.listarTodos());
        model.addAttribute("locais", localService.listarTodos());
        return "eventos";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Evento evento,
                         @RequestParam("cliente.id") Integer clienteId,
                         @RequestParam("local.id") Integer localId,
                         @RequestParam Map<String, String> allParams) {

        Cliente cliente = clienteService.buscarPorId(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        Local local = localService.buscarPorId(localId)
                .orElseThrow(() -> new RuntimeException("Local não encontrado"));

        evento.setCliente(cliente);
        evento.setLocal(local);

        evento.getServicos().clear();

        allParams.forEach((key, value) -> {
            if (key.startsWith("servico_") && "on".equals(value)) {
                Integer servicoId = Integer.parseInt(key.replace("servico_", ""));
                Integer quantidade = Integer.parseInt(allParams.getOrDefault("quantidade_" + servicoId, "1"));

                Servico servico = servicoService.buscarPorId(servicoId)
                        .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));

                EventoServico es = new EventoServico();
                es.setServico(servico);
                es.setQuantidade(quantidade);
                es.setEvento(evento);
                es.setId(new EventoServicoId(evento.getIdEvento(), servicoId));

                evento.getServicos().add(es);
            }
        });

        eventoService.salvar(evento);
        return "redirect:/eventos";
    }

    @PostMapping("/excluir/{id}")
    public String excluir(@PathVariable Integer id) {
        eventoService.deletar(id);
        return "redirect:/eventos";
    }

    @GetMapping("/buscar/{id}")
    @ResponseBody
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> buscarPorId(@PathVariable Integer id) {
        return eventoService.buscarPorId(id)
                .map(evento -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("idEvento", evento.getIdEvento());
                    response.put("titulo", evento.getTitulo());
                    response.put("dataEvento", evento.getDataEvento().toString());
                    response.put("horaInicio", evento.getHoraInicio().toString());
                    response.put("horaFim", evento.getHoraFim().toString());
                    response.put("valorTotal", evento.getValorTotal());
                    response.put("observacoes", evento.getObservacoes());

                    if(evento.getCliente() != null) {
                        Map<String, Object> clienteMap = new HashMap<>();
                        clienteMap.put("idCliente", evento.getCliente().getIdCliente());
                        clienteMap.put("nome", evento.getCliente().getNome());
                        response.put("cliente", clienteMap);
                    }

                    if(evento.getLocal() != null) {
                        Map<String, Object> localMap = new HashMap<>();
                        localMap.put("idLocal", evento.getLocal().getIdLocal());
                        localMap.put("nome", evento.getLocal().getNome());
                        response.put("local", localMap);
                    }

                    if(evento.getServicos() != null && !evento.getServicos().isEmpty()) {
                        List<Map<String, Object>> servicosList = evento.getServicos().stream()
                                .map(es -> {
                                    Map<String, Object> servicoMap = new HashMap<>();
                                    servicoMap.put("quantidade", es.getQuantidade());
                                    if(es.getServico() != null) {
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

    @GetMapping("/api/eventos")
    @ResponseBody
    public List<Map<String, Object>> eventosPorPeriodo(
            @RequestParam String inicio,
            @RequestParam String fim) {

        return eventoService.listarTodos().stream()
                .filter(e -> {
                    return !e.getDataEvento().isBefore(LocalDate.parse(inicio)) &&
                            !e.getDataEvento().isAfter(LocalDate.parse(fim));
                })
                .map(e -> {
                    Map<String, Object> mapa = new HashMap<>();
                    mapa.put("idEvento", e.getIdEvento());
                    mapa.put("titulo", e.getTitulo());
                    mapa.put("dataEvento", e.getDataEvento().toString());
                    return mapa;
                })
                .collect(Collectors.toList());
    }

}