package AgroBoi.service;

import AgroBoi.domain.entity.RegistroSanitario;
import AgroBoi.repository.RegistroSanitarioRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RegistroSanitarioService {
    private final RegistroSanitarioRepository repository;
    private final AnimalReferencia animais;
    public RegistroSanitarioService(RegistroSanitarioRepository repository, AnimalReferencia animais) {
        this.repository = repository; this.animais = animais;
    }
    public List<RegistroSanitario> listarTodos() { return repository.findAll(); }
    public Optional<RegistroSanitario> buscarPorId(Long id) { return repository.findById(Regras.obrigatorio(id, "ID")); }
    @Transactional
    public RegistroSanitario salvar(RegistroSanitario registro) {
        Regras.obrigatorio(registro, "Registro");
        if (registro.getId() != null) Regras.exigir(repository.existsById(registro.getId()), "Registro não encontrado.");
        registro.setAnimal(animais.buscar(registro.getAnimal(), true));
        animais.validarData(registro.getAnimal(), registro.getDataAplicacao());
        Regras.obrigatorio(registro.getTipo(), "Tipo");
        registro.setDescricao(Regras.texto(registro.getDescricao(), 255, "Descrição"));
        registro.setUnidadeDose(Regras.opcional(registro.getUnidadeDose(), 20, "Unidade da dose"));
        Regras.exigir((registro.getDose() == null) == (registro.getUnidadeDose() == null), "Dose e unidade devem ser informadas juntas.");
        if (registro.getDose() != null) Regras.decimal(registro.getDose(), 7, 3, "Dose");
        Regras.exigir(registro.getProximaAplicacao() == null || !registro.getProximaAplicacao().isBefore(registro.getDataAplicacao()), "Próxima aplicação anterior ao atendimento.");
        return repository.save(registro);
    }
    @Transactional
    public void excluir(Long id) {
        var registro = repository.findById(Regras.obrigatorio(id, "ID")).orElseThrow(() -> new RegraNegocioException("Registro não encontrado."));
        repository.delete(registro);
    }
}
