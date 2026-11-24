package AgroBoi.service;

import AgroBoi.domain.entity.Reproducao;
import AgroBoi.repository.ReproducaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReproducaoService {
    private final ReproducaoRepository repository;

    public ReproducaoService(ReproducaoRepository repository) {
        this.repository = repository;
    }

    public List<Reproducao> listarTodos() {
        return repository.findAll();
    }

    public Optional<Reproducao> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Reproducao salvar(Reproducao registro) {
        return repository.save(registro);
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }
}

