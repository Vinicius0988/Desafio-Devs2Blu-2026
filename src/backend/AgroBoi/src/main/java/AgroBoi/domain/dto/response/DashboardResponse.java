package AgroBoi.domain.dto.response;

import java.math.BigDecimal;
import java.util.List;

/** Indicadores do painel (RF13). Os nomes dos campos seguem o dashboard.js do frontend. */
public record DashboardResponse(
        long totalBovinos,
        long machos,
        long femeas,
        long vendidos,
        long mortos,
        long vacinacoesPendentes,
        long vacinacoesAtrasadas,
        long pesagensNoMes,
        long nascimentosNoMes,
        BigDecimal receitasNoMes,
        BigDecimal despesasNoMes,
        BigDecimal saldoNoMes,
        List<MovimentacaoResponse> movimentacoesRecentes
) {
}
