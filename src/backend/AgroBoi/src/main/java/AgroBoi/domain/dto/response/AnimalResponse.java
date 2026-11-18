package AgroBoi.demo.domain.dto.response;

import AgroBoi.demo.domain.enums.Sexo;
import AgroBoi.demo.domain.enums.StatusAnimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record AnimalResponse(
        Long id,
        String identificacao,
        String nome,
        Sexo sexo,
        LocalDate dataNascimento,
        Long racaId,
        Long maeId,
        Long paiId,
        StatusAnimal situacao,
        String observacoes,
        LocalDateTime criadoEm,
        Integer idadeEmMeses
) {
}
