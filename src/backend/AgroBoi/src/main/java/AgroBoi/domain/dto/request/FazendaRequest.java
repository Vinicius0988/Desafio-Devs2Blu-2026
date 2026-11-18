package AgroBoi.domain.dto.request;


public record FazendaRequest(
        String nome,
        String nomeResponsavel,
        String municipio,
        String uf,
        String endereco
) {
}
