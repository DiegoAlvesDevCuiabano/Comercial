package com.controle_comercial.controller;

import com.controle_comercial.model.entity.Servico;
import com.controle_comercial.service.ServicoService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/servicos")
public class ServicoController {

    private final ServicoService service;

    public ServicoController(ServicoService service) {
        this.service = service;
    }

    @GetMapping
    public String listarServicos(Model model) {
        model.addAttribute("servicos", service.listarTodos());
        return "servicos";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Servico servico) {
        service.salvar(servico);
        return "redirect:/servicos";
    }

    @PostMapping("/excluir/{id}")
    public String excluir(@PathVariable Integer id) {
        service.deletar(id);
        return "redirect:/servicos";
    }

    @GetMapping("/buscar/{id}")
    @ResponseBody
    public ResponseEntity<Servico> buscarPorId(@PathVariable Integer id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/salvar-multiplos")
    public ResponseEntity<?> salvarMultiplos(@RequestBody List<Servico> servicos) {
        try {
            List<Servico> salvos = servicos.stream()
                    .map(service::salvar)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(salvos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
