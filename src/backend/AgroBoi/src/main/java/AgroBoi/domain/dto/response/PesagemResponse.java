package AgroBoi.demo.domain.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PesagemResponse(
        Long id,
        Long animalId,
        LocalDateTime dataHora,
        BigDecimal pesoKg,
        String observacoes
) {
}
