package AgroBoi.service;

import AgroBoi.domain.dto.request.*;
import AgroBoi.domain.entity.*;
import AgroBoi.domain.enums.*;
import AgroBoi.repository.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@SpringBootTest
@ActiveProfiles("test")
class VendaIntegracaoTest {
    @jakarta.persistence.PersistenceContext jakarta.persistence.EntityManager entityManager;
    @Autowired VendaService vendas;
    @Autowired VendaRepository vendaRepo;
    @MockitoSpyBean ItemVendaRepository itens;
    @Autowired AnimalRepository animais;
    @Autowired MovimentacaoRepository movimentos;
    @Autowired PesagemRepository pesagens;
    @Autowired ReproducaoRepository reproducoes;
    @Autowired MovimentacaoService movimentacoes;
    private final LocalDate hoje = LocalDate.now();

    @BeforeEach void limpar() {
        movimentos.deleteAll(); itens.deleteAll(); vendaRepo.deleteAll();
        pesagens.deleteAll(); reproducoes.deleteAll(); animais.deleteAll();
    }

    private Animal animal(String identificacao) {
        Animal a = new Animal(); a.setIdentificacao(identificacao); a.setSexo(Sexo.FEMEA); a.setDataNascimento(hoje.minusYears(3));
        return animais.saveAndFlush(a);
    }

    private VendaRequest venda(Animal... animais) {
        return new VendaRequest(hoje, "Comprador", null,
            java.util.Arrays.stream(animais).map(a -> new ItemVendaRequest(a.getId(), new BigDecimal("1500.00"))).toList());
    }

    @Test void vendaGravaItensSaidasESituacao() {
        Animal a = animal("A"); Animal b = animal("B");
        Venda salva = vendas.salvar(venda(a, b));
        assertNotNull(salva.getId()); assertEquals(1, vendaRepo.count()); assertEquals(2, itens.count()); assertEquals(2, movimentos.count());
        assertEquals(StatusAnimal.VENDIDO, animais.findById(a.getId()).orElseThrow().getSituacao());
        assertEquals(StatusAnimal.VENDIDO, animais.findById(b.getId()).orElseThrow().getSituacao());
        assertTrue(movimentos.findAll().stream().allMatch(m -> m.getMotivo() == MotivoMovimentacao.VENDA && m.getItemVenda() != null));
    }

    @Test void falhaNoSegundoItemDesfazTodaVenda() {
        Animal a = animal("A"); Animal b = animal("B"); AtomicInteger chamadas = new AtomicInteger();
        doAnswer(invocation -> {
            if (chamadas.incrementAndGet() == 2) throw new IllegalStateException("Falha simulada");
            Itemvenda item = invocation.getArgument(0);
            entityManager.persist(item);
            entityManager.flush();
            return item;
        }).when(itens).save(any(Itemvenda.class));
        assertThrows(IllegalStateException.class, () -> vendas.salvar(venda(a, b)));
        assertEquals(2, chamadas.get()); assertEquals(0, vendaRepo.count()); assertEquals(0, itens.count()); assertEquals(0, movimentos.count());
        assertEquals(StatusAnimal.NA_FAZENDA, animais.findById(a.getId()).orElseThrow().getSituacao());
        assertEquals(StatusAnimal.NA_FAZENDA, animais.findById(b.getId()).orElseThrow().getSituacao());
    }

    @Test void rejeitaAnimalDuplicadoSemGravarVenda() {
        Animal a = animal("A");
        assertThrows(RegraNegocioException.class, () -> vendas.salvar(venda(a, a)));
        assertEquals(0, vendaRepo.count());
    }

    @Test void rejeitaVendaSemItens() {
        assertThrows(RegraNegocioException.class, () -> vendas.salvar(new VendaRequest(hoje, "Comprador", null, List.of())));
        assertEquals(0, vendaRepo.count());
    }

    @Test void naoVendeAnimalDuasVezes() {
        Animal a = animal("A"); vendas.salvar(venda(a));
        assertThrows(RegraNegocioException.class, () -> vendas.salvar(venda(a)));
        assertEquals(1, vendaRepo.count()); assertEquals(1, itens.count());
    }

    @Test void vendaAnteriorAPesagemEhRejeitada() {
        Animal a = animal("A"); Pesagem p = new Pesagem(); p.setAnimal(a); p.setPesoKg(BigDecimal.TEN); p.setDataHora(hoje.atStartOfDay()); pesagens.saveAndFlush(p);
        VendaRequest request = new VendaRequest(hoje.minusDays(1), "Comprador", null, venda(a).itens());
        assertThrows(RegraNegocioException.class, () -> vendas.salvar(request)); assertEquals(0, vendaRepo.count());
    }

    @Test void consultaDeHistoricoConsideraDesfechoSemReprodutorCadastrado() {
        Animal a = animal("A"); Reproducao r = new Reproducao(); r.setMatriz(a); r.setTipo(TipoReproducao.INSEMINACAO);
        r.setDataEvento(hoje.minusDays(20)); r.setResultado(ResultadoReproducao.PARTO); r.setDataDesfecho(hoje);
        reproducoes.saveAndFlush(r);
        assertTrue(reproducoes.existeEventoDepois(a.getId(), hoje.minusDays(1)));
        assertTrue(reproducoes.existeEventoAntes(a.getId(), hoje.minusDays(1)));
        assertFalse(reproducoes.existeEventoDepois(a.getId(), hoje));
        assertThrows(RegraNegocioException.class, () -> vendas.salvar(new VendaRequest(hoje.minusDays(1), "Comprador", null, venda(a).itens())));
    }

    @Test void entradaInicialNaoPodeSerPosteriorAPesagem() {
        Animal a = animal("A"); Pesagem p = new Pesagem(); p.setAnimal(a); p.setPesoKg(BigDecimal.TEN); p.setDataHora(hoje.minusDays(2).atStartOfDay()); pesagens.saveAndFlush(p);
        Movimentacao m = new Movimentacao(); m.setAnimal(a); m.setData(hoje); m.setTipo(TipoMovimentacao.ENTRADA); m.setMotivo(MotivoMovimentacao.COMPRA);
        assertThrows(RegraNegocioException.class, () -> movimentacoes.salvar(m)); assertEquals(0, movimentos.count());
    }
}
