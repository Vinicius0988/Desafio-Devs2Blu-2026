package AgroBoi.service;

import AgroBoi.domain.dto.request.UsuarioRequest;
import AgroBoi.domain.entity.Usuario;
import AgroBoi.repository.UsuarioRepository;
import java.util.*;
import java.nio.charset.StandardCharsets;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UsuarioService {
    private final UsuarioRepository repository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    public UsuarioService(UsuarioRepository repository) { this.repository=repository; }
    public List<Usuario> listarTodos() { return repository.findAll(); }
    public Optional<Usuario> buscarPorId(Long id) { return repository.findById(Regras.obrigatorio(id, "ID")); }
    @Transactional
    public Usuario salvar(UsuarioRequest request) { return gravar(null, request); }
    @Transactional
    public Usuario atualizar(Long id, UsuarioRequest request) { return gravar(Regras.obrigatorio(id, "ID"), request); }
    private Usuario gravar(Long id, UsuarioRequest request) {
        Regras.obrigatorio(request, "Usuário");
        Usuario usuario = id == null ? new Usuario() : repository.findById(id).orElseThrow(() -> new RegraNegocioException("Usuário não encontrado."));
        String email = Regras.texto(request.email(), 254, "Email").toLowerCase(Locale.ROOT);
        Regras.exigir(email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"), "Email inválido.");
        repository.findByEmailIgnoreCase(email).ifPresent(existente -> Regras.exigir(Objects.equals(existente.getId(), id), "Email já cadastrado."));
        usuario.setNome(Regras.texto(request.nome(), 150, "Nome")); usuario.setEmail(email);
        if (id == null || request.senha() != null) {
            Regras.exigir(request.senha() != null && !request.senha().isBlank(), "Senha obrigatória.");
            Regras.exigir(request.senha().getBytes(StandardCharsets.UTF_8).length <= 72, "Senha excede o limite de 72 bytes.");
            usuario.setSenhaHash(encoder.encode(request.senha()));
        }
        // Provisionamento administrativo deve ser protegido na futura camada de segurança.
        if (id == null) usuario.setPerfil("USUARIO_COMUM");
        return repository.save(usuario);
    }
    @Transactional
    public void excluir(Long id) {
        Usuario usuario = repository.findById(Regras.obrigatorio(id, "ID")).orElseThrow(() -> new RegraNegocioException("Usuário não encontrado."));
        Regras.exigir(!"ADMINISTRADOR".equals(usuario.getPerfil()), "Administrador não pode ser desativado por este fluxo.");
        usuario.setAtivo(false); repository.save(usuario);
    }
}
