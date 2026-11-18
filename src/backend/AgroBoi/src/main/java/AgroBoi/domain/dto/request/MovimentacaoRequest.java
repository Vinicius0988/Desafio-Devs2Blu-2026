package AgroBoi.domain.dto.request;

import AgroBoi.domain.enums.MotivoMovimentacao;
import AgroBoi.domain.enums.TipoMovimentacao;
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
