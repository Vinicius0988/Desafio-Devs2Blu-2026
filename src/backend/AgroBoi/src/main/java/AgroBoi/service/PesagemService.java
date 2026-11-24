package AgroBoi.service;

import AgroBoi.domain.entity.Pesagem;
import AgroBoi.repository.PesagemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PesagemService {
    private final PesagemRepository repository;

    public PesagemService(PesagemRepository repository) {
        this.repository = repository;
    }

    public List<Pesagem> listarTodos() {
        return repository.findAll();
    }

    public Optional<Pesagem> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Pesagem salvar(Pesagem registro) {
        return repository.save(registro);
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }
}

