package AgroBoi.domain.dto.response;

import java.time.Instant;

public record ErroResposta(
        Instant timestamp,
        int status,
        String mensagem
) {
    public static ErroResposta de(int status, String mensagem) {
        return new ErroResposta(Instant.now(), status, mensagem);
    }
}
