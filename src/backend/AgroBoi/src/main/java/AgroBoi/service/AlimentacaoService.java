package AgroBoi.service;

import AgroBoi.domain.entity.Alimentacao;
import AgroBoi.repository.AlimentacaoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AlimentacaoService {
    private final AlimentacaoRepository repository;
    private final AnimalReferencia animais;
    public AlimentacaoService(AlimentacaoRepository repository, AnimalReferencia animais) {
        this.repository = repository; this.animais = animais;
    }
    public List<Alimentacao> listarTodos() { return repository.findAll(); }
    public Optional<Alimentacao> buscarPorId(Long id) { return repository.findById(Regras.obrigatorio(id, "ID")); }
    @Transactional
    public Alimentacao salvar(Alimentacao registro) {
        Regras.obrigatorio(registro, "Registro");
        if (registro.getId() != null) Regras.exigir(repository.existsById(registro.getId()), "Registro não encontrado.");
        registro.setAnimal(animais.buscar(registro.getAnimal(), false));
        animais.validarData(registro.getAnimal(), registro.getData());
        registro.setDescricao(Regras.texto(registro.getDescricao(), 255, "Descrição"));
        registro.setUnidade(Regras.opcional(registro.getUnidade(), 20, "Unidade"));
        Regras.exigir((registro.getQuantidade() == null) == (registro.getUnidade() == null), "Quantidade e unidade devem ser informadas juntas.");
        if (registro.getQuantidade() != null) Regras.decimal(registro.getQuantidade(), 9, 3, "Quantidade");
        return repository.save(registro);
    }
    @Transactional
    public void excluir(Long id) {
        var registro = repository.findById(Regras.obrigatorio(id, "ID")).orElseThrow(() -> new RegraNegocioException("Registro não encontrado."));
        repository.delete(registro);
    }
}
