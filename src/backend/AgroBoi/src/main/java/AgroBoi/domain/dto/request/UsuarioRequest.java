package AgroBoi.domain.dto.request;


public record UsuarioRequest(
        String nome,
        String email,
        String senha
) {
    @Override
    public String toString() {
        return "UsuarioRequest[credenciais omitidas]";
    }
}
