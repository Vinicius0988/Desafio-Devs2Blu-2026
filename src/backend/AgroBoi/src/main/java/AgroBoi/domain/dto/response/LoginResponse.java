package AgroBoi.domain.dto.response;

public record LoginResponse(
        String token,
        String tipo,
        long expiraEmSegundos,
        UsuarioResponse usuario
) {
}
