package AgroBoi.service;

import AgroBoi.domain.entity.Raca;
import AgroBoi.repository.RacaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RacaService {
    private final RacaRepository repository;

    public RacaService(RacaRepository repository) {
        this.repository = repository;
    }

    public List<Raca> listarTodos() {
        return repository.findAll();
    }

    public Optional<Raca> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Raca salvar(Raca registro) {
        return repository.save(registro);
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }
}

