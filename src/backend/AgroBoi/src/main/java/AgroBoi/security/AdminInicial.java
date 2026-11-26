package AgroBoi.security;

import AgroBoi.domain.entity.Usuario;
import AgroBoi.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Cria o primeiro administrador quando ADMIN_EMAIL e ADMIN_PASSWORD estão definidos
 * e o e-mail ainda não existe. Não há cadastro público pela API.
 * Contas existentes não são promovidas nem têm sua senha substituída.
 */
@Component
public class AdminInicial implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(AdminInicial.class);

    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;
    private final String email;
    private final String senha;
    private final String nome;

    public AdminInicial(UsuarioRepository repository, PasswordEncoder encoder,
                        @Value("${agroboi.admin.email:}") String email,
                        @Value("${agroboi.admin.senha:}") String senha,
                        @Value("${agroboi.admin.nome:Administrador}") String nome) {
        this.repository = repository;
        this.encoder = encoder;
        this.email = email;
        this.senha = senha;
        this.nome = nome;
    }

    @Override
    public void run(String... args) {
        if (email.isBlank() || senha.isBlank()) return;
        String emailNormalizado = email.trim().toLowerCase();
        if (repository.findByEmailIgnoreCase(emailNormalizado).isPresent()) return;
        Usuario admin = new Usuario();
        admin.setNome(nome);
        admin.setEmail(emailNormalizado);
        admin.setSenhaHash(encoder.encode(senha));
        admin.setPerfil("ADMINISTRADOR");
        repository.save(admin);
        log.info("Administrador inicial criado: {}", emailNormalizado);
    }
}
