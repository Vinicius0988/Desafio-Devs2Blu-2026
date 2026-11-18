package AgroBoi.domain.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PesagemRequest(
        Long animalId,
        LocalDateTime dataHora,
        BigDecimal pesoKg,
        String observacoes
) {
}
