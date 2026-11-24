package AgroBoi.domain.dto.response;

import AgroBoi.domain.enums.CategoriaDespesa;
import AgroBoi.domain.enums.ResultadoReproducao;
import AgroBoi.domain.enums.Sexo;
import AgroBoi.domain.enums.StatusAnimal;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/** Respostas dos relatórios (RF14). */
public final class Relatorios {
    private Relatorios() {}

    public record Rebanho(
            LocalDate geradoEm,
            long total,
            Map<StatusAnimal, Long> porSituacao,
            Map<Sexo, Long> naFazendaPorSexo,
            Map<String, Long> naFazendaPorRaca
    ) {}

    public record Vacinacao(
            LocalDate geradoEm,
            long total,
            long atrasadas,
            long proximas30Dias,
            List<RegistroSanitarioResponse> registros
    ) {}

    public record PesagemAnimal(
            Long animalId,
            String identificacao,
            long pesagens,
            LocalDateTime primeiraData,
            BigDecimal primeiroPesoKg,
            LocalDateTime ultimaData,
            BigDecimal ultimoPesoKg,
            BigDecimal ganhoKg,
            BigDecimal ganhoMedioDiarioKg
    ) {}

    public record Pesagens(
            LocalDate geradoEm,
            LocalDate inicio,
            LocalDate fim,
            long totalPesagens,
            List<PesagemAnimal> animais
    ) {}

    public record Reproducao(
            LocalDate geradoEm,
            LocalDate inicio,
            LocalDate fim,
            long total,
            Map<ResultadoReproducao, Long> porResultado,
            List<ReproducaoResponse> eventos
    ) {}

    public record Financeiro(
            LocalDate geradoEm,
            LocalDate inicio,
            LocalDate fim,
            BigDecimal receitas,
            BigDecimal despesas,
            BigDecimal saldo,
            long animaisVendidos,
            Map<CategoriaDespesa, BigDecimal> despesasPorCategoria
    ) {}
}
