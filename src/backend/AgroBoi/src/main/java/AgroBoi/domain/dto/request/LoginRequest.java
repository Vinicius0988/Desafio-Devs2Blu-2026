package AgroBoi.domain.dto.request;

public record LoginRequest(
        String email,
        String senha
) {
    @Override
    public String toString() {
        return "LoginRequest[credenciais omitidas]";
    }
}
