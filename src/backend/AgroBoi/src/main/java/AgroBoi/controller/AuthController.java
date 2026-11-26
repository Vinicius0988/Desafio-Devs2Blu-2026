package AgroBoi.controller;

import AgroBoi.domain.dto.request.LoginRequest;
import AgroBoi.domain.dto.response.ErroResposta;
import AgroBoi.domain.dto.response.LoginResponse;
import AgroBoi.domain.dto.response.UsuarioResponse;
import AgroBoi.domain.entity.Usuario;
import AgroBoi.mapper.Mapeador;
import AgroBoi.security.JwtService;
import AgroBoi.service.AuthService;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/** Login público e consulta da própria conta. O fazendeiro é provisionado por AdminInicial. */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<Usuario> usuario = authService.autenticar(request.email(), request.senha());
        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ErroResposta.de(HttpStatus.UNAUTHORIZED.value(), "E-mail ou senha inválidos."));
        }
        Usuario u = usuario.get();
        return ResponseEntity.ok(new LoginResponse(jwtService.gerar(u), "Bearer",
                jwtService.getExpiracaoSegundos(), Mapeador.toResponse(u)));
    }

    @GetMapping("/me")
    public UsuarioResponse me(Authentication authentication) {
        return authService.buscarPorEmail(authentication.getName()).map(Mapeador::toResponse)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
    }
}
