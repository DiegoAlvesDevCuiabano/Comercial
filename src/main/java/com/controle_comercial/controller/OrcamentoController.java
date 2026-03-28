package com.controle_comercial.controller;

import com.controle_comercial.model.entity.Orcamento;
import com.controle_comercial.model.entity.StatusOrcamento;
import com.controle_comercial.service.ClienteService;
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

    public OrcamentoController(OrcamentoService orcamentoService,
                               ClienteService clienteService,
                               LocalService localService,
                               ServicoService servicoService) {
        this.orcamentoService = orcamentoService;
        this.clienteService = clienteService;
        this.localService = localService;
        this.servicoService = servicoService;
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

    @GetMapping("/buscar/{id}")
    @ResponseBody
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {
        return orcamentoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pdf/{id}")
    public ResponseEntity<InputStreamResource> gerarPdf(@PathVariable Integer id) {
        Orcamento orcamento = orcamentoService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Orçamento não encontrado"));

        try {
            ByteArrayInputStream bis = RelatorioGenerator.gerarPdfOrcamento(orcamento);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=" + orcamento.getNumeroOrcamento() + ".pdf");
            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF do orçamento", e);
        }
    }
}
