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

import jakarta.servlet.http.HttpServletRequest;

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

    // === VIEWERS (HTML wrapper com título correto) ===

    @GetMapping("/eventos")
    public String viewEventos(HttpServletRequest request, Model model) {
        String query = request.getQueryString();
        String rawUrl = "/comercial/relatorios/eventos/raw" + (query != null && !query.isBlank() ? "?" + query : "");
        model.addAttribute("pdfTitle", "Relatório de Eventos | UniSENAI");
        model.addAttribute("pdfUrl", rawUrl);
        return "pdf-viewer";
    }

    @GetMapping("/entidades")
    public String viewEntidades(Model model) {
        model.addAttribute("pdfTitle", "Relatório de Cadastros | UniSENAI");
        model.addAttribute("pdfUrl", "/comercial/relatorios/entidades/raw");
        return "pdf-viewer";
    }

    @GetMapping("/publico")
    public String viewPublico(HttpServletRequest request, Model model) {
        String query = request.getQueryString();
        String rawUrl = "/comercial/relatorios/publico/raw" + (query != null && !query.isBlank() ? "?" + query : "");
        model.addAttribute("pdfTitle", "Público Estimado | UniSENAI");
        model.addAttribute("pdfUrl", rawUrl);
        return "pdf-viewer";
    }

    @GetMapping("/horas")
    public String viewHoras(HttpServletRequest request, Model model) {
        String query = request.getQueryString();
        String rawUrl = "/comercial/relatorios/horas/raw" + (query != null && !query.isBlank() ? "?" + query : "");
        model.addAttribute("pdfTitle", "Horas Contratadas | UniSENAI");
        model.addAttribute("pdfUrl", rawUrl);
        return "pdf-viewer";
    }

    // === RAW PDF ENDPOINTS ===

    @GetMapping(value = "/eventos/raw", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> gerarRelatorioEventos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) Integer clienteId,
            @RequestParam(required = false) Integer localId,
            @RequestParam(required = false) StatusEvento status) {

        List<Evento> eventos = (dataInicio != null || dataFim != null || clienteId != null || localId != null || status != null)
                ? eventoService.listarComFiltros(dataInicio, dataFim, clienteId, localId, status)
                : eventoService.listarTodos();

        try {
            ByteArrayInputStream bis = RelatorioGenerator.gerarRelatorioEventos(eventos);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(org.springframework.http.ContentDisposition.inline().filename("UniSENAI_Relatorio_Eventos.pdf").build());
            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar relatório de eventos", e);
        }
    }

    @GetMapping(value = "/entidades/raw", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> gerarRelatorioEntidades() {
        List<Cliente> clientes = clienteService.listarTodos();
        List<Servico> servicos = servicoService.listarTodos();
        List<Local> locais = localService.listarTodos();

        try {
            ByteArrayInputStream bis = RelatorioGenerator.gerarRelatorioCompleto(clientes, servicos, locais);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(org.springframework.http.ContentDisposition.inline().filename("UniSENAI_Relatorio_Cadastros.pdf").build());
            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar relatório de entidades", e);
        }
    }

    @GetMapping(value = "/publico/raw", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> gerarRelatorioPublico(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {

        List<Evento> eventos = (dataInicio != null || dataFim != null)
                ? eventoService.listarComFiltros(dataInicio, dataFim, null, null, null)
                : eventoService.listarTodos();

        try {
            ByteArrayInputStream bis = RelatorioGenerator.gerarRelatorioPublico(eventos);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(org.springframework.http.ContentDisposition.inline().filename("UniSENAI_Publico_Estimado.pdf").build());
            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar relatório de público", e);
        }
    }

    @GetMapping(value = "/horas/raw", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> gerarRelatorioHoras(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {

        List<Evento> eventos = (dataInicio != null || dataFim != null)
                ? eventoService.listarComFiltros(dataInicio, dataFim, null, null, null)
                : eventoService.listarTodos();

        try {
            ByteArrayInputStream bis = RelatorioGenerator.gerarRelatorioHoras(eventos);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(org.springframework.http.ContentDisposition.inline().filename("UniSENAI_Horas_Contratadas.pdf").build());
            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar relatório de horas", e);
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
