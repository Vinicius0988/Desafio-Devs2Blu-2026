package AgroBoi.service;

import AgroBoi.domain.dto.response.Relatorios;
import AgroBoi.domain.entity.*;
import AgroBoi.domain.enums.*;
import AgroBoi.mapper.Mapeador;
import AgroBoi.repository.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Relatórios gerenciais (RF14). Todos aceitam período opcional (inicio/fim, inclusivos). */
@Service
@Transactional(readOnly = true)
public class RelatorioService {
    private final AnimalRepository animais;
    private final PesagemRepository pesagens;
    private final RegistroSanitarioRepository sanitarios;
    private final ReproducaoRepository reproducoes;
    private final DespesaRepository despesas;
    private final ItemVendaRepository itensVenda;

    public RelatorioService(AnimalRepository animais, PesagemRepository pesagens, RegistroSanitarioRepository sanitarios,
                            ReproducaoRepository reproducoes, DespesaRepository despesas, ItemVendaRepository itensVenda) {
        this.animais = animais;
        this.pesagens = pesagens;
        this.sanitarios = sanitarios;
        this.reproducoes = reproducoes;
        this.despesas = despesas;
        this.itensVenda = itensVenda;
    }

    private static void validarPeriodo(LocalDate inicio, LocalDate fim) {
        Regras.exigir(inicio == null || fim == null || !fim.isBefore(inicio), "A data final não pode ser anterior à inicial.");
    }

    private static boolean dentro(LocalDate data, LocalDate inicio, LocalDate fim) {
        return data != null && (inicio == null || !data.isBefore(inicio)) && (fim == null || !data.isAfter(fim));
    }

    public Relatorios.Rebanho rebanho() {
        Map<StatusAnimal, Long> porSituacao = new EnumMap<>(StatusAnimal.class);
        long total = 0;
        for (StatusAnimal situacao : StatusAnimal.values()) {
            long quantidade = animais.countBySituacao(situacao);
            porSituacao.put(situacao, quantidade);
            total += quantidade;
        }
        Map<Sexo, Long> porSexo = new EnumMap<>(Sexo.class);
        for (Sexo sexo : Sexo.values()) {
            porSexo.put(sexo, animais.countBySituacaoAndSexo(StatusAnimal.NA_FAZENDA, sexo));
        }
        Map<String, Long> porRaca = animais.findBySituacao(StatusAnimal.NA_FAZENDA).stream()
                .collect(Collectors.groupingBy(a -> a.getRaca() == null ? "Sem raça" : a.getRaca().getNome(),
                        TreeMap::new, Collectors.counting()));
        return new Relatorios.Rebanho(LocalDate.now(), total, porSituacao, porSexo, porRaca);
    }

    public Relatorios.Vacinacao vacinacao(LocalDate inicio, LocalDate fim) {
        validarPeriodo(inicio, fim);
        LocalDate hoje = LocalDate.now();
        List<RegistroSanitario> vacinas = sanitarios.findAll().stream()
                .filter(r -> r.getTipo() == TipoSanitario.VACINA)
                .filter(r -> dentro(r.getDataAplicacao(), inicio, fim))
                .sorted(Comparator.comparing(RegistroSanitario::getDataAplicacao).reversed())
                .toList();
        long atrasadas = vacinas.stream()
                .filter(r -> r.getProximaAplicacao() != null && r.getProximaAplicacao().isBefore(hoje)).count();
        long proximas = vacinas.stream()
                .filter(r -> r.getProximaAplicacao() != null && !r.getProximaAplicacao().isBefore(hoje)
                        && !r.getProximaAplicacao().isAfter(hoje.plusDays(30))).count();
        return new Relatorios.Vacinacao(hoje, vacinas.size(), atrasadas, proximas,
                vacinas.stream().map(Mapeador::toResponse).toList());
    }

    public Relatorios.Pesagens pesagens(LocalDate inicio, LocalDate fim) {
        validarPeriodo(inicio, fim);
        List<Pesagem> filtradas = pesagens.findAll().stream()
                .filter(p -> dentro(p.getDataHora().toLocalDate(), inicio, fim))
                .sorted(Comparator.comparing(Pesagem::getDataHora))
                .toList();
        Map<Long, List<Pesagem>> porAnimal = filtradas.stream()
                .collect(Collectors.groupingBy(p -> p.getAnimal().getId(), LinkedHashMap::new, Collectors.toList()));
        List<Relatorios.PesagemAnimal> resumo = new ArrayList<>();
        for (List<Pesagem> lista : porAnimal.values()) {
            Pesagem primeira = lista.get(0);
            Pesagem ultima = lista.get(lista.size() - 1);
            BigDecimal ganho = ultima.getPesoKg().subtract(primeira.getPesoKg());
            long dias = ChronoUnit.DAYS.between(primeira.getDataHora(), ultima.getDataHora());
            BigDecimal gmd = dias > 0 ? ganho.divide(BigDecimal.valueOf(dias), 3, RoundingMode.HALF_UP) : null;
            resumo.add(new Relatorios.PesagemAnimal(primeira.getAnimal().getId(), primeira.getAnimal().getIdentificacao(),
                    lista.size(), primeira.getDataHora(), primeira.getPesoKg(), ultima.getDataHora(), ultima.getPesoKg(),
                    ganho, gmd));
        }
        resumo.sort(Comparator.comparing(Relatorios.PesagemAnimal::identificacao));
        return new Relatorios.Pesagens(LocalDate.now(), inicio, fim, filtradas.size(), resumo);
    }

    public Relatorios.Reproducao reproducao(LocalDate inicio, LocalDate fim) {
        validarPeriodo(inicio, fim);
        List<AgroBoi.domain.entity.Reproducao> eventos = reproducoes.findAll().stream()
                .filter(r -> dentro(r.getDataEvento(), inicio, fim))
                .sorted(Comparator.comparing(AgroBoi.domain.entity.Reproducao::getDataEvento).reversed())
                .toList();
        Map<ResultadoReproducao, Long> porResultado = new EnumMap<>(ResultadoReproducao.class);
        for (ResultadoReproducao resultado : ResultadoReproducao.values()) porResultado.put(resultado, 0L);
        eventos.forEach(e -> porResultado.merge(e.getResultado(), 1L, Long::sum));
        return new Relatorios.Reproducao(LocalDate.now(), inicio, fim, eventos.size(), porResultado,
                eventos.stream().map(Mapeador::toResponse).toList());
    }

    /** Receitas = valor das vendas no período; despesas = despesas lançadas no período. */
    public Relatorios.Financeiro financeiro(LocalDate inicio, LocalDate fim) {
        LocalDate hoje = LocalDate.now();
        LocalDate de = inicio != null ? inicio : hoje.withDayOfMonth(1);
        LocalDate ate = fim != null ? fim : hoje;
        validarPeriodo(de, ate);
        List<Itemvenda> vendas = itensVenda.findByVendaDataBetween(de, ate);
        BigDecimal receitas = vendas.stream().map(Itemvenda::getValor).reduce(BigDecimal.ZERO, BigDecimal::add);
        List<Despesa> gastos = despesas.findByDataBetween(de, ate);
        BigDecimal totalDespesas = gastos.stream().map(Despesa::getValor).reduce(BigDecimal.ZERO, BigDecimal::add);
        Map<CategoriaDespesa, BigDecimal> porCategoria = new EnumMap<>(CategoriaDespesa.class);
        for (CategoriaDespesa categoria : CategoriaDespesa.values()) porCategoria.put(categoria, BigDecimal.ZERO);
        gastos.forEach(d -> porCategoria.merge(d.getCategoria(), d.getValor(), BigDecimal::add));
        return new Relatorios.Financeiro(hoje, de, ate, receitas, totalDespesas, receitas.subtract(totalDespesas),
                vendas.size(), porCategoria);
    }
}
