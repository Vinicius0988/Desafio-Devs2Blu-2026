package AgroBoi.mapper;

import AgroBoi.domain.dto.request.*;
import AgroBoi.domain.dto.response.*;
import AgroBoi.domain.entity.*;
import java.util.List;
import java.util.function.Function;
import org.hibernate.proxy.HibernateProxy;

/**
 * Converte DTOs em entidades (apenas com referências por ID, que os services
 * consultam e validam) e entidades em DTOs de resposta. Nunca expõe entidades
 * diretamente (principalmente Usuario, que contém o hash da senha).
 */
public final class Mapeador {
    private Mapeador() {}

    // ---------- utilitários ----------

    /** Lê o ID sem inicializar proxies lazy (open-in-view está desligado). */
    private static <T> Long idDe(T entidade, Function<T, Long> getter) {
        if (entidade == null) return null;
        if (entidade instanceof HibernateProxy proxy) {
            return (Long) proxy.getHibernateLazyInitializer().getIdentifier();
        }
        return getter.apply(entidade);
    }

    private static Long idAnimal(Animal a) { return idDe(a, Animal::getId); }

    private static Animal animalRef(Long id) {
        if (id == null) return null;
        Animal a = new Animal();
        a.setId(id);
        return a;
    }

    private static Raca racaRef(Long id) {
        if (id == null) return null;
        Raca r = new Raca();
        r.setId(id);
        return r;
    }

    // ---------- Raça ----------

    public static Raca toEntity(RacaRequest r, Long id) {
        Raca e = new Raca();
        e.setId(id);
        e.setNome(r.nome());
        return e;
    }

    public static RacaResponse toResponse(Raca e) {
        return new RacaResponse(e.getId(), e.getNome());
    }

    // ---------- Fazenda ----------

    public static Fazenda toEntity(FazendaRequest r) {
        Fazenda e = new Fazenda();
        e.setNome(r.nome());
        e.setNomeResponsavel(r.nomeResponsavel());
        e.setMunicipio(r.municipio());
        e.setUf(r.uf());
        e.setEndereco(r.endereco());
        return e;
    }

    public static FazendaResponse toResponse(Fazenda e) {
        return new FazendaResponse(e.getId(), e.getNome(), e.getNomeResponsavel(), e.getMunicipio(), e.getUf(), e.getEndereco());
    }

    // ---------- Usuário ----------

    public static UsuarioResponse toResponse(Usuario e) {
        return new UsuarioResponse(e.getId(), e.getNome(), e.getEmail(), e.getPerfil(), e.isAtivo(), e.getCriadoEm());
    }

    // ---------- Animal ----------

    public static Animal toEntity(AnimalRequest r, Long id) {
        Animal e = new Animal();
        e.setId(id);
        e.setIdentificacao(r.identificacao());
        e.setNome(r.nome());
        e.setSexo(r.sexo());
        e.setDataNascimento(r.dataNascimento());
        e.setRaca(racaRef(r.racaId()));
        e.setMae(animalRef(r.maeId()));
        e.setPai(animalRef(r.paiId()));
        // Sem situação informada, mantém o padrão da entidade (NA_FAZENDA).
        if (r.situacao() != null) e.setSituacao(r.situacao());
        e.setObservacoes(r.observacoes());
        return e;
    }

    public static AnimalResponse toResponse(Animal e) {
        return new AnimalResponse(e.getId(), e.getIdentificacao(), e.getNome(), e.getSexo(), e.getDataNascimento(),
                idDe(e.getRaca(), Raca::getId), idAnimal(e.getMae()), idAnimal(e.getPai()),
                e.getSituacao(), e.getObservacoes(), e.getCriadoEm(), e.getIdadeEmMeses());
    }

    // ---------- Pesagem ----------

    public static Pesagem toEntity(PesagemRequest r, Long id) {
        Pesagem e = new Pesagem();
        e.setId(id);
        e.setAnimal(animalRef(r.animalId()));
        e.setDataHora(r.dataHora());
        e.setPesoKg(r.pesoKg());
        e.setObservacoes(r.observacoes());
        return e;
    }

    public static PesagemResponse toResponse(Pesagem e) {
        return new PesagemResponse(e.getId(), idAnimal(e.getAnimal()), e.getDataHora(), e.getPesoKg(), e.getObservacoes());
    }

    // ---------- Registro sanitário (vacinas, medicamentos, procedimentos) ----------

    public static RegistroSanitario toEntity(RegistroSanitarioRequest r, Long id) {
        RegistroSanitario e = new RegistroSanitario();
        e.setId(id);
        e.setAnimal(animalRef(r.animalId()));
        e.setTipo(r.tipo());
        e.setDataAplicacao(r.dataAplicacao());
        e.setDescricao(r.descricao());
        e.setDose(r.dose());
        e.setUnidadeDose(r.unidadeDose());
        e.setProximaAplicacao(r.proximaAplicacao());
        e.setObservacoes(r.observacoes());
        return e;
    }

    public static RegistroSanitarioResponse toResponse(RegistroSanitario e) {
        return new RegistroSanitarioResponse(e.getId(), idAnimal(e.getAnimal()), e.getTipo(), e.getDataAplicacao(),
                e.getDescricao(), e.getDose(), e.getUnidadeDose(), e.getProximaAplicacao(), e.getObservacoes());
    }

    // ---------- Alimentação ----------

    public static Alimentacao toEntity(AlimentacaoRequest r, Long id) {
        Alimentacao e = new Alimentacao();
        e.setId(id);
        e.setAnimal(animalRef(r.animalId()));
        e.setData(r.data());
        e.setDescricao(r.descricao());
        e.setQuantidade(r.quantidade());
        e.setUnidade(r.unidade());
        e.setObservacoes(r.observacoes());
        return e;
    }

    public static AlimentacaoResponse toResponse(Alimentacao e) {
        return new AlimentacaoResponse(e.getId(), idAnimal(e.getAnimal()), e.getData(), e.getDescricao(),
                e.getQuantidade(), e.getUnidade(), e.getObservacoes());
    }

    // ---------- Reprodução ----------

    public static Reproducao toEntity(ReproducaoRequest r, Long id) {
        Reproducao e = new Reproducao();
        e.setId(id);
        e.setMatriz(animalRef(r.matrizId()));
        e.setReprodutor(animalRef(r.reprodutorId()));
        e.setReferenciaReprodutorExterno(r.referenciaReprodutorExterno());
        e.setTipo(r.tipo());
        e.setDataEvento(r.dataEvento());
        // Sem resultado informado, mantém o padrão da entidade (PENDENTE).
        if (r.resultado() != null) e.setResultado(r.resultado());
        e.setDataPrevistaParto(r.dataPrevistaParto());
        e.setDataDesfecho(r.dataDesfecho());
        e.setObservacoes(r.observacoes());
        return e;
    }

    public static ReproducaoResponse toResponse(Reproducao e) {
        return new ReproducaoResponse(e.getId(), idAnimal(e.getMatriz()), idAnimal(e.getReprodutor()),
                e.getReferenciaReprodutorExterno(), e.getTipo(), e.getDataEvento(), e.getResultado(),
                e.getDataPrevistaParto(), e.getDataDesfecho(), e.getObservacoes());
    }

    // ---------- Despesa ----------

    public static Despesa toEntity(DespesaRequest r, Long id) {
        Despesa e = new Despesa();
        e.setId(id);
        e.setAnimal(animalRef(r.animalId()));
        e.setCategoria(r.categoria());
        e.setDescricao(r.descricao());
        e.setData(r.data());
        e.setValor(r.valor());
        e.setObservacoes(r.observacoes());
        return e;
    }

    public static DespesaResponse toResponse(Despesa e) {
        return new DespesaResponse(e.getId(), idAnimal(e.getAnimal()), e.getCategoria(), e.getDescricao(),
                e.getData(), e.getValor(), e.getObservacoes());
    }

    // ---------- Movimentação ----------

    /**
     * O itemVendaId do request é ignorado de propósito: saídas por venda só podem
     * ser criadas pelo VendaService, que grava venda, itens e movimentações juntos.
     */
    public static Movimentacao toEntity(MovimentacaoRequest r) {
        Movimentacao e = new Movimentacao();
        e.setAnimal(animalRef(r.animalId()));
        e.setTipo(r.tipo());
        e.setMotivo(r.motivo());
        e.setData(r.data());
        e.setOrigemDestino(r.origemDestino());
        e.setObservacoes(r.observacoes());
        return e;
    }

    public static MovimentacaoResponse toResponse(Movimentacao e) {
        return new MovimentacaoResponse(e.getId(), idAnimal(e.getAnimal()), e.getTipo(), e.getMotivo(), e.getData(),
                idDe(e.getItemVenda(), Itemvenda::getId), e.getOrigemDestino(), e.getObservacoes());
    }

    // ---------- Venda e itens ----------

    public static ItemVendaResponse toResponse(Itemvenda e) {
        return new ItemVendaResponse(e.getId(), idDe(e.getVenda(), Venda::getId), idAnimal(e.getAnimal()), e.getValor());
    }

    public static VendaResponse toResponse(Venda e, List<Itemvenda> itens) {
        return new VendaResponse(e.getId(), e.getData(), e.getComprador(), e.getObservacoes(),
                itens.stream().map(Mapeador::toResponse).toList());
    }
}
