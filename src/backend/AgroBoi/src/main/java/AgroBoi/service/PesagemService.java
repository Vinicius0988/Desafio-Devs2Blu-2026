package AgroBoi.service;

import AgroBoi.domain.entity.Pesagem;
import AgroBoi.repository.PesagemRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PesagemService {
    private final PesagemRepository repository;
    private final AnimalReferencia animais;
    public PesagemService(PesagemRepository repository, AnimalReferencia animais) {
        this.repository = repository; this.animais = animais;
    }
    public List<Pesagem> listarTodos() { return repository.findAll(); }
    public Optional<Pesagem> buscarPorId(Long id) { return repository.findById(Regras.obrigatorio(id, "ID")); }
    @Transactional
    public Pesagem salvar(Pesagem registro) {
        Regras.obrigatorio(registro, "Registro");
        if (registro.getId() != null) Regras.exigir(repository.existsById(registro.getId()), "Registro não encontrado.");
        registro.setAnimal(animais.buscar(registro.getAnimal(), true));
        Regras.obrigatorio(registro.getDataHora(), "Data e hora");
        Regras.exigir(!registro.getDataHora().isAfter(java.time.LocalDateTime.now()), "Pesagem futura não permitida.");
        animais.validarData(registro.getAnimal(), registro.getDataHora().toLocalDate());
        Regras.decimal(registro.getPesoKg(), 7, 3, "Peso");
        return repository.save(registro);
    }
    @Transactional
    public void excluir(Long id) {
        var registro = repository.findById(Regras.obrigatorio(id, "ID")).orElseThrow(() -> new RegraNegocioException("Registro não encontrado."));
        repository.delete(registro);
    }
}
