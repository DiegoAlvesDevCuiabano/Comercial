package com.controle_comercial.controller;

import com.controle_comercial.model.dto.EventoEditDTO;
import com.controle_comercial.model.entity.Evento;
import com.controle_comercial.service.ClienteService;
import com.controle_comercial.service.EventoService;
import com.controle_comercial.service.LocalService;
import com.controle_comercial.service.ServicoService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
        eventoService.salvarEventoComServicos(evento, clienteId, localId, allParams);
        return "redirect:/eventos";
    }

    @PostMapping("/excluir/{id}")
    public String excluir(@PathVariable Integer id) {
        eventoService.deletar(id);
        return "redirect:/eventos";
    }

    @GetMapping("/buscar/{id}")
    @ResponseBody
    public ResponseEntity<EventoEditDTO> buscarPorId(@PathVariable Integer id) {
        return eventoService.buscarEventoParaEdicao(id);
    }

    @GetMapping("/api/eventos")
    @ResponseBody
    public List<Map<String, Object>> eventosPorPeriodo(@RequestParam String inicio, @RequestParam String fim) {
        return eventoService.listarEventosPorPeriodo(inicio, fim);
    }

}
