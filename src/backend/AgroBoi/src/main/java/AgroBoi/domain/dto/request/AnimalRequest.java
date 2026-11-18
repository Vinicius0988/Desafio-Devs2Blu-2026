package AgroBoi.demo.domain.dto.request;

import AgroBoi.demo.domain.enums.Sexo;
import AgroBoi.demo.domain.enums.StatusAnimal;
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
