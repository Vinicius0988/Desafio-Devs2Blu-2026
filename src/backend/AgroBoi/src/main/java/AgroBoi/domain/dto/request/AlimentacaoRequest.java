package AgroBoi.domain.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AlimentacaoRequest(
        Long animalId,
        LocalDate data,
        String descricao,
        BigDecimal quantidade,
        String unidade,
        String observacoes
) {
}
