package AgroBoi.service;

import AgroBoi.domain.entity.Fazenda;
import AgroBoi.repository.FazendaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FazendaService {
    private final FazendaRepository repository;

    public FazendaService(FazendaRepository repository) {
        this.repository = repository;
    }

    public List<Fazenda> listarTodos() {
        return repository.findAll();
    }

    public Optional<Fazenda> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Fazenda salvar(Fazenda registro) {
        return repository.save(registro);
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }
}

