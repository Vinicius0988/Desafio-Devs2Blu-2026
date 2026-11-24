package AgroBoi.service;

import AgroBoi.domain.entity.Fazenda;
import AgroBoi.repository.FazendaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@org.springframework.transaction.annotation.Transactional(readOnly = true)
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

    @org.springframework.transaction.annotation.Transactional
    public Fazenda salvar(Fazenda registro) {
        Regras.obrigatorio(registro, "Registro");
        Regras.exigir(registro.getId() == null || registro.getId() == 1L, "A instalação admite uma única fazenda.");
        registro.setId(1L);
        registro.setNome(Regras.texto(registro.getNome(), 150, "Nome"));
        registro.setNomeResponsavel(Regras.texto(registro.getNomeResponsavel(), 150, "Responsável"));
        registro.setMunicipio(Regras.opcional(registro.getMunicipio(), 100, "Município"));
        registro.setEndereco(Regras.opcional(registro.getEndereco(), 255, "Endereço"));
        registro.setUf(Regras.opcional(registro.getUf(), 2, "UF"));
        if (registro.getUf() != null) { registro.setUf(registro.getUf().toUpperCase(java.util.Locale.ROOT)); Regras.exigir(registro.getUf().matches("[A-Z]{2}"), "UF inválida."); }
        return repository.save(registro);
    }

    @org.springframework.transaction.annotation.Transactional
    public void excluir(Long id) {
        throw new RegraNegocioException("A fazenda não pode ser excluída.");
    }
}

