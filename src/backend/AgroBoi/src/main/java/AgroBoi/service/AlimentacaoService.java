package AgroBoi.service;

import AgroBoi.domain.entity.Alimentacao;
import AgroBoi.repository.AlimentacaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlimentacaoService {
    private final AlimentacaoRepository repository;

    public AlimentacaoService(AlimentacaoRepository repository) {
        this.repository = repository;
    }

    public List<Alimentacao> listarTodos() {
        return repository.findAll();
    }

    public Optional<Alimentacao> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Alimentacao salvar(Alimentacao registro) {
        return repository.save(registro);
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }
}

