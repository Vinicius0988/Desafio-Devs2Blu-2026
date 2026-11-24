package AgroBoi.controller;

import AgroBoi.domain.dto.response.Relatorios;
import AgroBoi.service.RelatorioService;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

/** RF14: relatórios. Datas no formato AAAA-MM-DD, ex.: /api/relatorios/financeiro?inicio=2026-01-01&fim=2026-12-31 */
@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {
    private final RelatorioService service;

    public RelatorioController(RelatorioService service) {
        this.service = service;
    }

    @GetMapping("/rebanho")
    public Relatorios.Rebanho rebanho() {
        return service.rebanho();
    }

    @GetMapping("/vacinacao")
    public Relatorios.Vacinacao vacinacao(
            @RequestParam(name = "inicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(name = "fim", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return service.vacinacao(inicio, fim);
    }

    @GetMapping("/pesagens")
    public Relatorios.Pesagens pesagens(
            @RequestParam(name = "inicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(name = "fim", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return service.pesagens(inicio, fim);
    }

    @GetMapping("/reproducao")
    public Relatorios.Reproducao reproducao(
            @RequestParam(name = "inicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(name = "fim", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return service.reproducao(inicio, fim);
    }

    @GetMapping("/financeiro")
    public Relatorios.Financeiro financeiro(
            @RequestParam(name = "inicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(name = "fim", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return service.financeiro(inicio, fim);
    }
}
