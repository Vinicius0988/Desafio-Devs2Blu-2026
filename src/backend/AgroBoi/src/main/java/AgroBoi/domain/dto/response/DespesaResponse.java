package AgroBoi.domain.dto.response;

import AgroBoi.domain.enums.CategoriaDespesa;
import java.math.BigDecimal;
import java.time.LocalDate;

public record DespesaResponse(
        Long id,
        Long animalId,
        CategoriaDespesa categoria,
        String descricao,
        LocalDate data,
        BigDecimal valor,
        String observacoes
) {
}
