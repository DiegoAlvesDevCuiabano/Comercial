package com.controle_comercial.controller;

import com.controle_comercial.model.entity.Cliente;
import com.controle_comercial.service.ClienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    @GetMapping
    public String listarClientes(Model model) {
        model.addAttribute("clientes", service.listarTodos());
        return "clientes";
    }

    // Atualize o endpoint de salvar para incluir o context path
    @PostMapping("/salvar")
    @ResponseBody
    public ResponseEntity<?> salvarCliente(@RequestBody Cliente cliente) {
        try {
            Cliente savedCliente = service.salvar(cliente);
            return ResponseEntity.ok(savedCliente);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Erro ao salvar cliente: " + e.getMessage()));
        }
    }

    @DeleteMapping("/excluir/{id}")
    @ResponseBody
    public void excluirCliente(@PathVariable Integer id) {
        service.deletar(id);
    }

    @GetMapping("/buscar/{id}")
    @ResponseBody
    public ResponseEntity<Cliente> buscarClientePorId(@PathVariable Integer id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
