package com.controle_comercial.controller;

import com.controle_comercial.model.entity.Orcamento;
import com.controle_comercial.model.entity.StatusOrcamento;
import com.controle_comercial.service.ClienteService;
import com.controle_comercial.service.EmailService;
import com.controle_comercial.service.LocalService;
import com.controle_comercial.service.OrcamentoService;
import com.controle_comercial.service.ServicoService;
import com.controle_comercial.util.RelatorioGenerator;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayInputStream;
import java.util.Map;

@Controller
@RequestMapping("/orcamentos")
public class OrcamentoController {

    private final OrcamentoService orcamentoService;
    private final ClienteService clienteService;
    private final LocalService localService;
    private final ServicoService servicoService;
    private final EmailService emailService;

    public OrcamentoController(OrcamentoService orcamentoService,
                               ClienteService clienteService,
                               LocalService localService,
                               ServicoService servicoService,
                               EmailService emailService) {
        this.orcamentoService = orcamentoService;
        this.clienteService = clienteService;
        this.localService = localService;
        this.servicoService = servicoService;
        this.emailService = emailService;
    }

    @GetMapping
    public String listarOrcamentos(@RequestParam(required = false) StatusOrcamento status, Model model) {
        if (status != null) {
            model.addAttribute("orcamentos", orcamentoService.listarPorStatus(status));
        } else {
            model.addAttribute("orcamentos", orcamentoService.listarTodos());
        }
        model.addAttribute("clientes", clienteService.listarTodos());
        model.addAttribute("locais", localService.listarTodos());
        model.addAttribute("servicos", servicoService.listarTodos());
        model.addAttribute("statusList", StatusOrcamento.values());
        model.addAttribute("statusFiltro", status);
        return "orcamentos";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Orcamento orcamento,
                         @RequestParam("cliente.id") Integer clienteId,
                         @RequestParam("local.id") Integer localId,
                         @RequestParam Map<String, String> allParams,
                         RedirectAttributes redirectAttributes) {
        orcamentoService.salvarOrcamentoComServicos(orcamento, clienteId, localId, allParams);
        redirectAttributes.addFlashAttribute("success", "Orçamento salvo com sucesso!");
        return "redirect:/orcamentos";
    }

    @PostMapping("/excluir/{id}")
    public String excluir(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        orcamentoService.deletar(id);
        redirectAttributes.addFlashAttribute("success", "Orçamento excluído com sucesso!");
        return "redirect:/orcamentos";
    }

    @PostMapping("/status/{id}")
    public String atualizarStatus(@PathVariable Integer id,
                                  @RequestParam StatusOrcamento status,
                                  RedirectAttributes redirectAttributes) {
        orcamentoService.atualizarStatus(id, status);
        redirectAttributes.addFlashAttribute("success", "Status atualizado para " + status.getDescricao());
        return "redirect:/orcamentos";
    }

    @PostMapping("/converter/{id}")
    public String converterParaEvento(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        orcamentoService.converterParaEvento(id);
        redirectAttributes.addFlashAttribute("success", "Evento criado a partir do orçamento!");
        return "redirect:/eventos";
    }

    @GetMapping("/novo")
    public String novoOrcamento(Model model) {
        model.addAttribute("clientes", clienteService.listarTodos());
        model.addAttribute("locais", localService.listarTodos());
        model.addAttribute("servicos", servicoService.listarTodos());
        return "orcamento-novo";
    }

    @GetMapping("/buscar/{id}")
    @ResponseBody
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {
        return orcamentoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/enviar-email/{id}")
    public String enviarEmail(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            Orcamento orcamento = orcamentoService.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Orçamento não encontrado"));
            emailService.enviarOrcamentoPorEmail(orcamento);

            // Marcar como ENVIADO automaticamente se estava em RASCUNHO
            if (orcamento.getStatus() == StatusOrcamento.RASCUNHO) {
                orcamentoService.atualizarStatus(id, StatusOrcamento.ENVIADO);
            }

            redirectAttributes.addFlashAttribute("success", "Email enviado para " + orcamento.getCliente().getEmail());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao enviar email: " + e.getMessage());
        }
        return "redirect:/orcamentos";
    }

    @GetMapping("/pdf/{id}")
    public String viewPdf(@PathVariable Integer id, Model model) {
        Orcamento orcamento = orcamentoService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Orçamento não encontrado"));
        model.addAttribute("pdfTitle", "Proposta " + orcamento.getNumeroOrcamento() + " | UniSENAI");
        model.addAttribute("pdfUrl", "/comercial/orcamentos/pdf/raw/" + id);
        return "pdf-viewer";
    }

    @GetMapping(value = "/pdf/raw/{id}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> gerarPdf(@PathVariable Integer id) {
        Orcamento orcamento = orcamentoService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Orçamento não encontrado"));

        try {
            ByteArrayInputStream bis = RelatorioGenerator.gerarPdfOrcamento(orcamento);
            String filename = "UniSENAI_Proposta_" + orcamento.getNumeroOrcamento() + ".pdf";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(org.springframework.http.ContentDisposition.inline().filename(filename).build());
            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF do orçamento", e);
        }
    }
}
