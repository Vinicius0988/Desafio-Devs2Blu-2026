package AgroBoi.domain.dto.request;

import AgroBoi.domain.enums.TipoSanitario;
import java.math.BigDecimal;
import java.time.LocalDate;

public record RegistroSanitarioRequest(
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
