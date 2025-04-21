package com.controle_comercial.controller;

import com.controle_comercial.model.entity.Evento;
import com.controle_comercial.service.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/eventos")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    @GetMapping("/listar")
    @ResponseBody
    public List<Evento> listarEventos() {
        return eventoService.listarTodos();
    }

    @GetMapping("/{id}")
    public String detalhesEvento(@PathVariable Integer id, Model model) {
        Evento evento = eventoService.buscarPorId(id);
        model.addAttribute("evento", evento);
        return "eventos/detalhes"; // Renderiza o arquivo templates/eventos/detalhes.html
    }

    @GetMapping("/data")
    public String listarPorData(@RequestParam("data") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data, Model model) {
        List<Evento> eventos = eventoService.listarPorData(data);
        model.addAttribute("eventos", eventos);
        model.addAttribute("data", data);
        return "eventos/lista";
    }

}

