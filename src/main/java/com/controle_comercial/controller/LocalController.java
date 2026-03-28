package com.controle_comercial.controller;

import com.controle_comercial.model.entity.Local;
import com.controle_comercial.model.entity.Local.TipoLocal;
import com.controle_comercial.service.LocalService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/local")
public class LocalController {

    private final LocalService service;

    public LocalController(LocalService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("locais", service.listarTodos());
        model.addAttribute("tipos", Arrays.asList(TipoLocal.values()));
        return "local";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Local local, RedirectAttributes redirectAttributes) {
        service.salvar(local);
        redirectAttributes.addFlashAttribute("success", "Local salvo com sucesso!");
        return "redirect:/local";
    }

    @PostMapping("/excluir/{id}")
    public String excluir(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        service.deletar(id);
        redirectAttributes.addFlashAttribute("success", "Local excluído com sucesso!");
        return "redirect:/local";
    }

    @GetMapping("/buscar/{id}")
    @ResponseBody
    public ResponseEntity<Local> buscarPorId(@PathVariable Integer id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
