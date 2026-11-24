package AgroBoi.service;

import AgroBoi.domain.entity.RegistroSanitario;
import AgroBoi.repository.RegistroSanitarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RegistroSanitarioService {
    private final RegistroSanitarioRepository repository;

    public RegistroSanitarioService(RegistroSanitarioRepository repository) {
        this.repository = repository;
    }

    public List<RegistroSanitario> listarTodos() {
        return repository.findAll();
    }

    public Optional<RegistroSanitario> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public RegistroSanitario salvar(RegistroSanitario registro) {
        return repository.save(registro);
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }
}

