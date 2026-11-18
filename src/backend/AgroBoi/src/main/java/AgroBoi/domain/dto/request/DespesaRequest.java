package AgroBoi.demo.domain.dto.request;

import AgroBoi.demo.domain.enums.CategoriaDespesa;
import java.math.BigDecimal;
import java.time.LocalDate;

public record DespesaRequest(
        Long animalId,
        CategoriaDespesa categoria,
        String descricao,
        LocalDate data,
        BigDecimal valor,
        String observacoes
) {
}
