package AgroBoi.service;

import AgroBoi.domain.entity.Movimentacao;
import AgroBoi.repository.MovimentacaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MovimentacaoService {
    private final MovimentacaoRepository repository;

    public MovimentacaoService(MovimentacaoRepository repository) {
        this.repository = repository;
    }

    public List<Movimentacao> listarTodos() {
        return repository.findAll();
    }

    public Optional<Movimentacao> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Movimentacao salvar(Movimentacao registro) {
        return repository.save(registro);
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }
}

