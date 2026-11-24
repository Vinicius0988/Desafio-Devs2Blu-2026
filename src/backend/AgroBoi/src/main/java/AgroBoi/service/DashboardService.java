package AgroBoi.service;

import AgroBoi.domain.dto.response.DashboardResponse;
import AgroBoi.domain.entity.Despesa;
import AgroBoi.domain.entity.Itemvenda;
import AgroBoi.domain.enums.Sexo;
import AgroBoi.domain.enums.StatusAnimal;
import AgroBoi.mapper.Mapeador;
import AgroBoi.repository.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Indicadores do painel principal (RF13). */
@Service
@Transactional(readOnly = true)
public class DashboardService {
    private final AnimalRepository animais;
    private final PesagemRepository pesagens;
    private final RegistroSanitarioRepository sanitarios;
    private final DespesaRepository despesas;
    private final ItemVendaRepository itensVenda;
    private final MovimentacaoRepository movimentacoes;

    public DashboardService(AnimalRepository animais, PesagemRepository pesagens, RegistroSanitarioRepository sanitarios,
                            DespesaRepository despesas, ItemVendaRepository itensVenda, MovimentacaoRepository movimentacoes) {
        this.animais = animais;
        this.pesagens = pesagens;
        this.sanitarios = sanitarios;
        this.despesas = despesas;
        this.itensVenda = itensVenda;
        this.movimentacoes = movimentacoes;
    }

    public DashboardResponse resumo() {
        LocalDate hoje = LocalDate.now();
        LocalDate inicioMes = hoje.withDayOfMonth(1);
        LocalDate fimMes = hoje.withDayOfMonth(hoje.lengthOfMonth());

        BigDecimal receitas = itensVenda.findByVendaDataBetween(inicioMes, fimMes).stream()
                .map(Itemvenda::getValor).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal gastos = despesas.findByDataBetween(inicioMes, fimMes).stream()
                .map(Despesa::getValor).reduce(BigDecimal.ZERO, BigDecimal::add);

        return new DashboardResponse(
                animais.countBySituacao(StatusAnimal.NA_FAZENDA),
                animais.countBySituacaoAndSexo(StatusAnimal.NA_FAZENDA, Sexo.MACHO),
                animais.countBySituacaoAndSexo(StatusAnimal.NA_FAZENDA, Sexo.FEMEA),
                animais.countBySituacao(StatusAnimal.VENDIDO),
                animais.countBySituacao(StatusAnimal.MORTO),
                sanitarios.countByProximaAplicacaoBetween(hoje, hoje.plusDays(30)),
                sanitarios.countByProximaAplicacaoBefore(hoje),
                pesagens.countByDataHoraBetween(inicioMes.atStartOfDay(), fimMes.atTime(LocalTime.MAX)),
                animais.countByDataNascimentoBetween(inicioMes, fimMes),
                receitas,
                gastos,
                receitas.subtract(gastos),
                movimentacoes.findTop10ByOrderByDataDescIdDesc().stream().map(Mapeador::toResponse).toList());
    }
}
