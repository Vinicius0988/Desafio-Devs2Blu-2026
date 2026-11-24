package AgroBoi.service;

import AgroBoi.domain.dto.request.VendaRequest;
import AgroBoi.domain.entity.*;
import AgroBoi.domain.enums.*;
import AgroBoi.repository.*;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class VendaService {
    private final VendaRepository repository;
    private final ItemVendaRepository itens;
    private final MovimentacaoRepository movimentos;
    private final AnimalRepository animais;
    private final AnimalReferencia referencias;
    private final MovimentacaoService movimentacoes;
    public VendaService(VendaRepository repository, ItemVendaRepository itens, MovimentacaoRepository movimentos,
        AnimalRepository animais, AnimalReferencia referencias, MovimentacaoService movimentacoes) {
        this.repository=repository; this.itens=itens; this.movimentos=movimentos;
        this.animais=animais; this.referencias=referencias; this.movimentacoes=movimentacoes;
    }
    public List<Venda> listarTodos() { return repository.findAll(); }
    public Optional<Venda> buscarPorId(Long id) { return repository.findById(Regras.obrigatorio(id, "ID")); }
    @Transactional
    public Venda salvar(VendaRequest request) {
        Regras.obrigatorio(request, "Venda"); Regras.data(request.data(), "Data da venda");
        String comprador = Regras.texto(request.comprador(), 150, "Comprador");
        Regras.exigir(request.itens() != null && !request.itens().isEmpty(), "A venda precisa de pelo menos um animal.");
        Set<Long> ids = new HashSet<>();
        for (var item : request.itens()) {
            Regras.obrigatorio(item, "Item"); Regras.obrigatorio(item.animalId(), "Animal");
            Regras.exigir(ids.add(item.animalId()), "Animal repetido na venda.");
            Regras.decimal(item.valor(), 12, 2, "Valor de venda");
        }
        Map<Long,Animal> selecionados = new HashMap<>();
        // Ordem estável de bloqueio reduz deadlocks em vendas concorrentes.
        for (Long id : ids.stream().sorted().toList()) {
            Animal animal = referencias.buscarId(id);
            Regras.exigir(animal.getSituacao() == StatusAnimal.NA_FAZENDA, "Animal indisponível para venda.");
            movimentacoes.validarSaida(animal, request.data());
            selecionados.put(id, animal);
        }
        Venda venda = new Venda(); venda.setData(request.data()); venda.setComprador(comprador); venda.setObservacoes(request.observacoes());
        venda = repository.save(venda);
        for (var entrada : request.itens()) {
            Animal animal = selecionados.get(entrada.animalId());
            Itemvenda item = new Itemvenda(); item.setVenda(venda); item.setAnimal(animal); item.setValor(entrada.valor()); item = itens.save(item);
            Movimentacao movimento = new Movimentacao(); movimento.setAnimal(animal); movimento.setTipo(TipoMovimentacao.SAIDA);
            movimento.setMotivo(MotivoMovimentacao.VENDA); movimento.setData(request.data()); movimento.setItemVenda(item); movimento.setOrigemDestino(comprador);
            movimentos.save(movimento); animal.setSituacao(StatusAnimal.VENDIDO); animais.save(animal);
        }
        return venda;
    }
    public void excluir(Long id) { throw new RegraNegocioException("Vendas confirmadas não podem ser excluídas pelo CRUD; exigem fluxo de estorno."); }
}
