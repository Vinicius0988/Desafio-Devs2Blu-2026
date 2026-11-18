package AgroBoi.domain.dto.request;

import AgroBoi.domain.enums.Sexo;
import AgroBoi.domain.enums.StatusAnimal;
import java.time.LocalDate;

public record AnimalRequest(
        String identificacao,
        String nome,
        Sexo sexo,
        LocalDate dataNascimento,
        Long racaId,
        Long maeId,
        Long paiId,
        StatusAnimal situacao,
        String observacoes
) {
}
