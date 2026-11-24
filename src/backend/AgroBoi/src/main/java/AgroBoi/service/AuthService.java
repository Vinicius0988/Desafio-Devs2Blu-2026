package AgroBoi.service;

import AgroBoi.domain.entity.Usuario;
import AgroBoi.repository.UsuarioRepository;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Verifica e-mail e senha (RF02). Usuários desativados não entram. */
@Service
@Transactional(readOnly = true)
public class AuthService {
    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;

    public AuthService(UsuarioRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    public Optional<Usuario> autenticar(String email, String senha) {
        if (email == null || email.isBlank() || senha == null) return Optional.empty();
        return repository.findByEmailIgnoreCase(email.trim())
                .filter(Usuario::isAtivo)
                .filter(usuario -> encoder.matches(senha, usuario.getSenhaHash()));
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return repository.findByEmailIgnoreCase(email);
    }
}
