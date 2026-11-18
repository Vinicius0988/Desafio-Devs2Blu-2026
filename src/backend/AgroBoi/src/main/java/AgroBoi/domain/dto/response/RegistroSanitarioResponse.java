package AgroBoi.domain.dto.response;

import AgroBoi.domain.enums.TipoSanitario;
import java.math.BigDecimal;
import java.time.LocalDate;

public record RegistroSanitarioResponse(
        Long id,
        Long animalId,
        TipoSanitario tipo,
        LocalDate dataAplicacao,
        String descricao,
        BigDecimal dose,
        String unidadeDose,
        LocalDate proximaAplicacao,
        String observacoes
) {
}
