package AgroBoi.domain.dto.response;

import java.time.LocalDateTime;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        String perfil,
        boolean ativo,
        LocalDateTime criadoEm
) {
}
