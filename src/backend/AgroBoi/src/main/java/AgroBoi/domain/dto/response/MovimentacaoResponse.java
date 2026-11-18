package AgroBoi.demo.domain.dto.response;

import AgroBoi.demo.domain.enums.MotivoMovimentacao;
import AgroBoi.demo.domain.enums.TipoMovimentacao;
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
