package com.calendario.controller;

import org.springframework.web.bind.annotation.*;

import com.calendario.model.CalendarioMes;
import com.calendario.service.CalendarioService;

import java.util.List;

@RestController
@RequestMapping("/api/calendario")
@CrossOrigin(origins = "*") // permite o HTML chamar a API
public class CalendarioController {

    private final CalendarioService service;

    // Spring injeta o Service automaticamente (injeção de dependência)
    public CalendarioController(CalendarioService service) {
        this.service = service;
    }

    // GET /api/calendario?ano=2026&mes=4
    @GetMapping
    public CalendarioMes getCalendario(
            @RequestParam int ano,
            @RequestParam int mes) {

        List<List<Integer>> semanas = service.gerarCalendario(ano, mes);
        String nomeMes = service.getNomeMes(mes);
        return new CalendarioMes(ano, mes, nomeMes, semanas);
    }
}