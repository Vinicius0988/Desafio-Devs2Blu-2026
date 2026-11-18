package AgroBoi.demo.domain.dto.response;

import java.math.BigDecimal;

public record ItemVendaResponse(
        Long id,
        Long vendaId,
        Long animalId,
        BigDecimal valor
) {
}
