package AgroBoi.demo.domain.dto.response;

import AgroBoi.demo.domain.enums.TipoSanitario;
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
