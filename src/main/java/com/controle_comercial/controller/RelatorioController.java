package com.controle_comercial.controller;

import com.controle_comercial.model.entity.*;
import com.controle_comercial.service.*;
import com.controle_comercial.util.RelatorioGenerator;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/relatorios")
public class RelatorioController {

    private final EventoService eventoService;
    private final ClienteService clienteService;
    private final ServicoService servicoService;
    private final LocalService localService;

    public RelatorioController(EventoService eventoService,
                               ClienteService clienteService,
                               ServicoService servicoService,
                               LocalService localService) {
        this.eventoService = eventoService;
        this.clienteService = clienteService;
        this.servicoService = servicoService;
        this.localService = localService;
    }

    @GetMapping("/eventos")
    public ResponseEntity<InputStreamResource> gerarRelatorioEventos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) Integer clienteId,
            @RequestParam(required = false) Integer localId,
            @RequestParam(required = false) StatusEvento status) {

        List<Evento> eventos;
        boolean temFiltro = dataInicio != null || dataFim != null || clienteId != null || localId != null || status != null;

        if (temFiltro) {
            eventos = eventoService.listarComFiltros(dataInicio, dataFim, clienteId, localId, status);
        } else {
            eventos = eventoService.listarTodos();
        }

        try {
            ByteArrayInputStream bis = RelatorioGenerator.gerarRelatorioEventos(eventos);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=relatorio_eventos.pdf");

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(bis));
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar relatório de eventos", e);
        }
    }

    @GetMapping("/entidades")
    public ResponseEntity<InputStreamResource> gerarRelatorioEntidades() {
        List<Cliente> clientes = clienteService.listarTodos();
        List<Servico> servicos = servicoService.listarTodos();
        List<Local> locais = localService.listarTodos();

        try {
            ByteArrayInputStream bis = RelatorioGenerator.gerarRelatorioCompleto(clientes, servicos, locais);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=relatorio_entidades.pdf");

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(bis));
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar relatório de entidades", e);
        }
    }

    @GetMapping
    public String relatorios(Model model) {
        model.addAttribute("clientes", clienteService.listarTodos());
        model.addAttribute("locais", localService.listarTodos());
        model.addAttribute("statusList", StatusEvento.values());
        return "relatorios";
    }

}
