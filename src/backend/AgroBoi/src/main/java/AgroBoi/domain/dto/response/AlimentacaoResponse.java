package AgroBoi.demo.domain.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AlimentacaoResponse(
        Long id,
        Long animalId,
        LocalDate data,
        String descricao,
        BigDecimal quantidade,
        String unidade,
        String observacoes
) {
}
