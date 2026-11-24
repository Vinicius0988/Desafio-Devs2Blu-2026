package AgroBoi.service;

import AgroBoi.domain.entity.Raca;
import AgroBoi.repository.RacaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@org.springframework.transaction.annotation.Transactional(readOnly = true)
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

    @org.springframework.transaction.annotation.Transactional
    public Raca salvar(Raca registro) {
        Regras.obrigatorio(registro, "Registro");
        if (registro.getId() != null) Regras.exigir(repository.existsById(registro.getId()), "Raça não encontrada.");
        registro.setNome(Regras.texto(registro.getNome(), 100, "Nome"));
        repository.findByNomeIgnoreCase(registro.getNome()).ifPresent(existente -> {
            Regras.exigir(java.util.Objects.equals(existente.getId(), registro.getId()), "Raça já cadastrada.");
        });
        return repository.save(registro);
    }

    @org.springframework.transaction.annotation.Transactional
    public void excluir(Long id) {
        var registro = repository.findById(Regras.obrigatorio(id, "ID")).orElseThrow(() -> new RegraNegocioException("Raça não encontrada."));
        repository.delete(registro);
    }
}

