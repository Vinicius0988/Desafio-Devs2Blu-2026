package AgroBoi.service;

import AgroBoi.domain.entity.*;
import AgroBoi.domain.enums.*;
import AgroBoi.repository.*;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MovimentacaoService {
    private final MovimentacaoRepository repository;
    private final AnimalReferencia animais;
    private final AnimalRepository animalRepository;
    private final HistoricoAnimal historicos;
    public MovimentacaoService(MovimentacaoRepository repository, AnimalReferencia animais, AnimalRepository animalRepository, HistoricoAnimal historicos) {
        this.historicos = historicos;
        this.repository = repository; this.animais = animais; this.animalRepository = animalRepository;
    }
    public List<Movimentacao> listarTodos() { return repository.findAll(); }
    public Optional<Movimentacao> buscarPorId(Long id) { return repository.findById(Regras.obrigatorio(id, "ID")); }
    @Transactional
    public Movimentacao salvar(Movimentacao registro) {
        Regras.obrigatorio(registro, "Movimentação");
        Regras.exigir(registro.getId() == null, "Movimentações confirmadas não podem ser alteradas pelo CRUD.");
        Regras.obrigatorio(registro.getTipo(), "Tipo"); Regras.obrigatorio(registro.getMotivo(), "Motivo");
        Regras.exigir(registro.getMotivo() != MotivoMovimentacao.VENDA && registro.getItemVenda() == null, "Saída por venda deve ser registrada pelo serviço de vendas.");
        Animal animal = animais.buscar(registro.getAnimal(), true);
        validarCronologia(animal, registro.getData());
        boolean entrada = registro.getTipo() == TipoMovimentacao.ENTRADA;
        Regras.exigir(registro.getMotivo() == MotivoMovimentacao.OUTRO ||
            (entrada && (registro.getMotivo() == MotivoMovimentacao.COMPRA || registro.getMotivo() == MotivoMovimentacao.NASCIMENTO)) ||
            (!entrada && registro.getMotivo() == MotivoMovimentacao.MORTE), "Tipo e motivo incompatíveis.");
        var historico = repository.findByAnimalIdOrderByDataAscIdAsc(animal.getId());
        boolean entradaInicial = entrada && historico.isEmpty() && animal.getSituacao() == StatusAnimal.NA_FAZENDA;
        Regras.exigir(animal.getSituacao() != StatusAnimal.MORTO, "Animal morto não pode ser movimentado.");
        Regras.exigir(entrada ? entradaInicial || animal.getSituacao() != StatusAnimal.NA_FAZENDA : animal.getSituacao() == StatusAnimal.NA_FAZENDA, "Entrada ou saída incompatível com a situação atual.");
        if (entradaInicial) historicos.validarEntradaInicial(animal.getId(), registro.getData());
        if (!entrada) historicos.validarSaida(animal.getId(), registro.getData());
        if (registro.getMotivo() == MotivoMovimentacao.NASCIMENTO) {
            Regras.exigir(historico.isEmpty(), "Nascimento já possui histórico de movimentação.");
            Regras.exigir(animal.getDataNascimento() != null && animal.getDataNascimento().equals(registro.getData()), "Data de nascimento incompatível.");
        }
        if (registro.getMotivo() == MotivoMovimentacao.OUTRO) Regras.texto(registro.getObservacoes(), 10000, "Motivo da movimentação");
        registro.setOrigemDestino(Regras.opcional(registro.getOrigemDestino(), 255, "Origem/destino"));
        registro.setAnimal(animal);
        animal.setSituacao(entrada ? StatusAnimal.NA_FAZENDA : registro.getMotivo() == MotivoMovimentacao.MORTE ? StatusAnimal.MORTO : StatusAnimal.SAIDA_OUTRA);
        animalRepository.save(animal);
        return repository.save(registro);
    }
    void validarCronologia(Animal animal, java.time.LocalDate data) {
        Regras.data(data, "Data da movimentação");
        Regras.exigir(animal.getDataNascimento() == null || !data.isBefore(animal.getDataNascimento()), "Movimentação anterior ao nascimento.");
        var historico = repository.findByAnimalIdOrderByDataAscIdAsc(animal.getId());
        Regras.exigir(historico.isEmpty() || !data.isBefore(historico.getLast().getData()), "Movimentação anterior ao último evento. Correções históricas exigem revalidação específica.");
    }
    void validarSaida(Animal animal, java.time.LocalDate data) {
        validarCronologia(animal, data);
        historicos.validarSaida(animal.getId(), data);
    }
    public void excluir(Long id) { throw new RegraNegocioException("Movimentações confirmadas devem ser preservadas."); }
}
