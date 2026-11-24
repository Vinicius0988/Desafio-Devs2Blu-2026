package AgroBoi.service;

import AgroBoi.domain.entity.*;
import AgroBoi.domain.enums.*;
import AgroBoi.repository.*;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AnimalService {
    private final AnimalRepository repository;
    private final RacaRepository racas;
    private final AnimalReferencia referencias;
    public AnimalService(AnimalRepository repository, RacaRepository racas, AnimalReferencia referencias) {
        this.repository = repository; this.racas = racas; this.referencias = referencias;
    }
    public List<Animal> listarTodos() { return repository.findAll(); }
    public Optional<Animal> buscarPorId(Long id) { return repository.findById(Regras.obrigatorio(id, "ID")); }
    /** RF15: texto livre em identificação, nome ou raça; texto vazio devolve todos. */
    public List<Animal> pesquisar(String busca) {
        String termo = busca == null ? "" : busca.replace("%", "").replace("_", "").trim();
        return termo.isEmpty() ? repository.findAll() : repository.pesquisar(termo);
    }
    @Transactional
    public Animal salvar(Animal registro) {
        Regras.obrigatorio(registro, "Animal");
        registro.setIdentificacao(Regras.texto(registro.getIdentificacao(), 50, "Identificação").toUpperCase(Locale.ROOT));
        registro.setNome(Regras.opcional(registro.getNome(), 100, "Nome"));
        Regras.obrigatorio(registro.getSexo(), "Sexo");
        if (registro.getDataNascimento() != null) Regras.data(registro.getDataNascimento(), "Nascimento");
        repository.findByIdentificacaoIgnoreCase(registro.getIdentificacao()).ifPresent(existente -> {
            Regras.exigir(Objects.equals(existente.getId(), registro.getId()), "Identificação já cadastrada.");
        });
        if (registro.getId() != null) {
            Animal atual = referencias.buscarId(registro.getId());
            Regras.exigir(atual.getSituacao() == registro.getSituacao(), "Altere a situação por movimentação ou venda.");
            // Alterações de nascimento/sexo exigem revalidar todo o histórico e descendentes.
            Regras.exigir(atual.getSexo() == registro.getSexo() && Objects.equals(atual.getDataNascimento(), registro.getDataNascimento()),
                "Sexo e nascimento não podem ser alterados pelo cadastro comum após a criação.");
            registro.setCriadoEm(atual.getCriadoEm());
        } else {
            Regras.exigir(registro.getSituacao() == null || registro.getSituacao() == StatusAnimal.NA_FAZENDA, "Novo animal deve estar na fazenda.");
            registro.setSituacao(StatusAnimal.NA_FAZENDA);
        }
        if (registro.getRaca() != null) registro.setRaca(racas.findById(Regras.obrigatorio(registro.getRaca().getId(), "ID da raça"))
            .orElseThrow(() -> new RegraNegocioException("Raça não encontrada.")));
        registro.setMae(genitor(registro, registro.getMae(), Sexo.FEMEA));
        registro.setPai(genitor(registro, registro.getPai(), Sexo.MACHO));
        return repository.save(registro);
    }
    private Animal genitor(Animal filho, Animal referencia, Sexo sexo) {
        if (referencia == null) return null;
        Animal genitor = referencias.buscar(referencia, true);
        Regras.exigir(genitor.getSexo() == sexo, "Sexo do genitor incompatível.");
        Regras.exigir(filho.getDataNascimento() == null || genitor.getDataNascimento() == null || genitor.getDataNascimento().isBefore(filho.getDataNascimento()), "Genitor deve ter nascido antes do filho.");
        Set<Long> visitados = new HashSet<>();
        Deque<Animal> fila = new ArrayDeque<>(); fila.add(genitor);
        while (!fila.isEmpty()) {
            Animal atual = fila.remove();
            Regras.exigir(!Objects.equals(filho.getId(), atual.getId()), "A genealogia não pode conter ciclos.");
            if (!visitados.add(atual.getId())) continue;
            if (atual.getMae() != null) fila.add(atual.getMae());
            if (atual.getPai() != null) fila.add(atual.getPai());
        }
        return genitor;
    }
    public void excluir(Long id) {
        throw new RegraNegocioException("Preserve o cadastro e registre uma saída ou morte por movimentação.");
    }
}
