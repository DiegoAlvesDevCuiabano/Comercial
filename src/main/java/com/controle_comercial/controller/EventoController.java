package com.controle_comercial.controller;

import com.controle_comercial.model.entity.*;
import com.controle_comercial.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

        // Carrega cliente e local
        Cliente cliente = clienteService.buscarPorId(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        Local local = localService.buscarPorId(localId)
                .orElseThrow(() -> new RuntimeException("Local não encontrado"));

        evento.setCliente(cliente);
        evento.setLocal(local);

        // Processa os serviços com quantidades
        Set<EventoServico> servicos = new HashSet<>();
        allParams.forEach((key, value) -> {
            if (key.startsWith("servico_") && "on".equals(value)) {
                Integer servicoId = Integer.parseInt(key.replace("servico_", ""));
                Integer quantidade = Integer.parseInt(allParams.getOrDefault("quantidade_" + servicoId, "1"));

                Servico servico = servicoService.buscarPorId(servicoId)
                        .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));

                EventoServico es = new EventoServico();
                es.setServico(servico);
                es.setQuantidade(quantidade);
                servicos.add(es);
            }
        });

        evento.setServicos(servicos);
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
    public ResponseEntity<Evento> buscarPorId(@PathVariable Integer id) {
        return eventoService.buscarPorId(id)
                .map(evento -> {
                    // Força o carregamento das associações
                    if(evento.getServicos() != null) {
                        evento.getServicos().size();
                    }
                    return ResponseEntity.ok(evento);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}