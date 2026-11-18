package AgroBoi.demo.domain.dto.request;

import AgroBoi.demo.domain.enums.MotivoMovimentacao;
import AgroBoi.demo.domain.enums.TipoMovimentacao;
import java.time.LocalDate;

public record MovimentacaoRequest(
        Long animalId,
        TipoMovimentacao tipo,
        MotivoMovimentacao motivo,
        LocalDate data,
        Long itemVendaId,
        String origemDestino,
        String observacoes
) {
}
