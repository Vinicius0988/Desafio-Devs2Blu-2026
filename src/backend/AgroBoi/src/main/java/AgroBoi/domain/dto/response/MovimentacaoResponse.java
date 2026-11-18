package AgroBoi.domain.dto.response;

import AgroBoi.domain.enums.MotivoMovimentacao;
import AgroBoi.domain.enums.TipoMovimentacao;
import java.time.LocalDate;

public record MovimentacaoResponse(
        Long id,
        Long animalId,
        TipoMovimentacao tipo,
        MotivoMovimentacao motivo,
        LocalDate data,
        Long itemVendaId,
        String origemDestino,
        String observacoes
) {
}
