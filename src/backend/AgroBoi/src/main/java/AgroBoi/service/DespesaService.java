package AgroBoi.service;

import AgroBoi.domain.entity.Despesa;
import AgroBoi.repository.DespesaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DespesaService {
    private final DespesaRepository repository;
    private final AnimalReferencia animais;
    public DespesaService(DespesaRepository repository, AnimalReferencia animais) {
        this.repository = repository; this.animais = animais;
    }
    public List<Despesa> listarTodos() { return repository.findAll(); }
    public Optional<Despesa> buscarPorId(Long id) { return repository.findById(Regras.obrigatorio(id, "ID")); }
    @Transactional
    public Despesa salvar(Despesa registro) {
        Regras.obrigatorio(registro, "Registro");
        if (registro.getId() != null) Regras.exigir(repository.existsById(registro.getId()), "Registro não encontrado.");
        registro.setAnimal(animais.buscar(registro.getAnimal(), false));
        Regras.data(registro.getData(), "Data da despesa");
        Regras.obrigatorio(registro.getCategoria(), "Categoria");
        registro.setDescricao(Regras.texto(registro.getDescricao(), 255, "Descrição"));
        Regras.decimal(registro.getValor(), 12, 2, "Valor");
        return repository.save(registro);
    }
    @Transactional
    public void excluir(Long id) {
        var registro = repository.findById(Regras.obrigatorio(id, "ID")).orElseThrow(() -> new RegraNegocioException("Registro não encontrado."));
        repository.delete(registro);
    }
}
