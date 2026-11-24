package AgroBoi.service;

import AgroBoi.domain.entity.Reproducao;
import AgroBoi.repository.ReproducaoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ReproducaoService {
    private final ReproducaoRepository repository;
    private final AnimalReferencia animais;
    public ReproducaoService(ReproducaoRepository repository, AnimalReferencia animais) {
        this.repository = repository; this.animais = animais;
    }
    public List<Reproducao> listarTodos() { return repository.findAll(); }
    public Optional<Reproducao> buscarPorId(Long id) { return repository.findById(Regras.obrigatorio(id, "ID")); }
    @Transactional
    public Reproducao salvar(Reproducao registro) {
        Regras.obrigatorio(registro, "Registro");
        if (registro.getId() != null) Regras.exigir(repository.existsById(registro.getId()), "Registro não encontrado.");
        registro.setMatriz(animais.buscar(registro.getMatriz(), true));
        registro.setReprodutor(animais.buscar(registro.getReprodutor(), false));
        animais.validarData(registro.getMatriz(), registro.getDataEvento());
        Regras.exigir(registro.getMatriz().getSexo() == AgroBoi.domain.enums.Sexo.FEMEA, "Matriz deve ser fêmea.");
        if (registro.getReprodutor() != null) {
            Regras.exigir(registro.getReprodutor().getSexo() == AgroBoi.domain.enums.Sexo.MACHO, "Reprodutor deve ser macho.");
            animais.validarData(registro.getReprodutor(), registro.getDataEvento());
        }
        registro.setReferenciaReprodutorExterno(Regras.opcional(registro.getReferenciaReprodutorExterno(), 150, "Reprodutor externo"));
        Regras.exigir(registro.getReprodutor() == null || registro.getReferenciaReprodutorExterno() == null, "Escolha reprodutor cadastrado ou externo.");
        Regras.obrigatorio(registro.getTipo(), "Tipo");
        Regras.obrigatorio(registro.getResultado(), "Resultado");
        Regras.exigir(registro.getDataPrevistaParto() == null || !registro.getDataPrevistaParto().isBefore(registro.getDataEvento()), "Previsão anterior ao evento.");
        boolean encerrado = registro.getResultado() == AgroBoi.domain.enums.ResultadoReproducao.PARTO || registro.getResultado() == AgroBoi.domain.enums.ResultadoReproducao.PERDA_GESTACIONAL;
        Regras.exigir(encerrado == (registro.getDataDesfecho() != null), "Data de desfecho obrigatória somente para parto ou perda gestacional.");
        if (encerrado) { animais.validarData(registro.getMatriz(), registro.getDataDesfecho()); Regras.exigir(!registro.getDataDesfecho().isBefore(registro.getDataEvento()), "Desfecho anterior ao evento."); }
        return repository.save(registro);
    }
    @Transactional
    public void excluir(Long id) {
        var registro = repository.findById(Regras.obrigatorio(id, "ID")).orElseThrow(() -> new RegraNegocioException("Registro não encontrado."));
        repository.delete(registro);
    }
}
