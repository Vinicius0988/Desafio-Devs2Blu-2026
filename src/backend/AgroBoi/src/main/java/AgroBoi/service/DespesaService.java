package AgroBoi.service;

import AgroBoi.domain.entity.Despesa;
import AgroBoi.repository.DespesaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DespesaService {
    private final DespesaRepository repository;

    public DespesaService(DespesaRepository repository) {
        this.repository = repository;
    }

    public List<Despesa> listarTodos() {
        return repository.findAll();
    }

    public Optional<Despesa> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Despesa salvar(Despesa registro) {
        return repository.save(registro);
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }
}

