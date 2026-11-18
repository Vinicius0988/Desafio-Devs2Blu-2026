package AgroBoi.domain.dto.request;

import java.math.BigDecimal;

public record ItemVendaRequest(
        Long animalId,
        BigDecimal valor
) {
}
