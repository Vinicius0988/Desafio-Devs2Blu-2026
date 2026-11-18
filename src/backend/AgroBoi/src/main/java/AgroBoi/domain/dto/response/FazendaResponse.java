package AgroBoi.demo.domain.dto.response;


public record FazendaResponse(
        Long id,
        String nome,
        String nomeResponsavel,
        String municipio,
        String uf,
        String endereco
) {
}
