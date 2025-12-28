package com.controle_comercial.controller;

import com.controle_comercial.model.entity.Cliente;
import com.controle_comercial.service.ClienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    @PostMapping("/salvar-multiplos")
    public ResponseEntity<?> salvarMultiplosClientes(@RequestBody List<Cliente> clientes) {
        try {
            List<Cliente> clientesSalvos = clientes.stream()
                    .map(service::salvar)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(clientesSalvos);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Erro ao salvar clientes: " + e.getMessage()));
        }
    }

    @GetMapping
    public String listarClientes(Model model) {
        model.addAttribute("clientes", service.listarTodos());
        return "clientes";
    }

    @PostMapping("/salvar")
    public String salvarCliente(@ModelAttribute Cliente cliente) {
        service.salvar(cliente);
        return "redirect:/clientes";
    }

    @PostMapping("/excluir/{id}")
    public String excluirCliente(@PathVariable Integer id) {
        service.deletar(id);
        return "redirect:/clientes";
    }

    @GetMapping("/buscar/{id}")
    @ResponseBody
    public ResponseEntity<Cliente> buscarClientePorId(@PathVariable Integer id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
