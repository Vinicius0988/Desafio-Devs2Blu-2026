package AgroBoi.service;

import AgroBoi.domain.dto.request.*;
import AgroBoi.domain.entity.*;
import AgroBoi.domain.enums.*;
import AgroBoi.repository.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegrasServicesTest {
    private final AnimalRepository animais = mock(AnimalRepository.class);
    private final MovimentacaoRepository movimentos = mock(MovimentacaoRepository.class);
    private final AnimalReferencia referencias = new AnimalReferencia(animais, movimentos);
    private final LocalDate hoje = LocalDate.now();

    private Animal animal(long id, Sexo sexo) {
        Animal animal = new Animal();
        animal.setId(id); animal.setIdentificacao("B" + id); animal.setSexo(sexo);
        animal.setDataNascimento(hoje.minusYears(3));
        when(animais.buscarComBloqueio(id)).thenReturn(Optional.of(animal));
        return animal;
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "1.001", "1000000000000"})
    void rejeitaValoresMonetariosInvalidos(String valor) {
        assertThrows(RegraNegocioException.class, () -> Regras.decimal(new BigDecimal(valor), 12, 2, "Valor"));
    }

    @Test void aceitaValorComZerosDecimaisExtras() {
        assertDoesNotThrow(() -> Regras.decimal(new BigDecimal("100.0000"), 12, 2, "Valor"));
    }

    @Test void rejeitaReferenciaInexistente() {
        assertThrows(RegraNegocioException.class, () -> referencias.buscarId(99L));
    }

    @Test void rejeitaEventoAnteriorAoNascimento() {
        Animal a = animal(1, Sexo.FEMEA);
        assertThrows(RegraNegocioException.class, () -> referencias.validarData(a, a.getDataNascimento().minusDays(1)));
    }

    @Test void historicoPermiteDiaDaSaidaMasNaoDiaPosterior() {
        Animal a = animal(1, Sexo.FEMEA); a.setSituacao(StatusAnimal.VENDIDO);
        Movimentacao saida = new Movimentacao(); saida.setTipo(TipoMovimentacao.SAIDA); saida.setData(hoje.minusDays(2));
        when(movimentos.findByAnimalIdOrderByDataAscIdAsc(1L)).thenReturn(List.of(saida));
        assertDoesNotThrow(() -> referencias.validarData(a, hoje.minusDays(2)));
        assertThrows(RegraNegocioException.class, () -> referencias.validarData(a, hoje));
    }

    @Test void rejeitaEventoNoIntervaloForaDaFazenda() {
        Animal a = animal(1, Sexo.FEMEA);
        Movimentacao saida = new Movimentacao(); saida.setTipo(TipoMovimentacao.SAIDA); saida.setData(hoje.minusDays(10));
        Movimentacao entrada = new Movimentacao(); entrada.setTipo(TipoMovimentacao.ENTRADA); entrada.setData(hoje.minusDays(2));
        when(movimentos.findByAnimalIdOrderByDataAscIdAsc(1L)).thenReturn(List.of(saida, entrada));
        assertThrows(RegraNegocioException.class, () -> referencias.validarData(a, hoje.minusDays(5)));
        assertDoesNotThrow(() -> referencias.validarData(a, hoje));
    }

    @Test void impedeCicloNaGenealogia() {
        Animal filho = animal(1, Sexo.FEMEA); filho.setDataNascimento(null);
        Animal mae = animal(2, Sexo.FEMEA); mae.setMae(filho);
        filho.setMae(mae);
        AnimalService service = new AnimalService(animais, mock(RacaRepository.class), referencias);
        assertThrows(RegraNegocioException.class, () -> service.salvar(filho));
        verify(animais, never()).save(any());
    }

    @Test void impedePaiComSexoIncompativel() {
        Animal pai = animal(2, Sexo.FEMEA);
        Animal filho = new Animal(); filho.setIdentificacao("novo"); filho.setSexo(Sexo.MACHO); filho.setPai(pai);
        assertThrows(RegraNegocioException.class, () -> new AnimalService(animais, mock(RacaRepository.class), referencias).salvar(filho));
    }

    @Test void normalizaIdentificacaoDeAnimalNovo() {
        Animal a = new Animal(); a.setIdentificacao("  br-01  "); a.setSexo(Sexo.FEMEA);
        when(animais.save(any())).thenAnswer(i -> i.getArgument(0));
        Animal salvo = new AnimalService(animais, mock(RacaRepository.class), referencias).salvar(a);
        assertEquals("BR-01", salvo.getIdentificacao()); assertEquals(StatusAnimal.NA_FAZENDA, salvo.getSituacao());
    }

    @Test void rejeitaIdentificacaoDuplicada() {
        Animal existente = animal(1, Sexo.MACHO);
        when(animais.findByIdentificacaoIgnoreCase("B1")).thenReturn(Optional.of(existente));
        Animal novo = new Animal(); novo.setIdentificacao("b1"); novo.setSexo(Sexo.MACHO);
        assertThrows(RegraNegocioException.class, () -> new AnimalService(animais, mock(RacaRepository.class), referencias).salvar(novo));
    }

    @Test void fazendaUsaIdentificadorUnico() {
        FazendaRepository repo = mock(FazendaRepository.class);
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));
        Fazenda f = new Fazenda(); f.setId(null); f.setNome("Fazenda"); f.setNomeResponsavel("Dono"); f.setUf("sc");
        new FazendaService(repo).salvar(f);
        assertEquals(1L, f.getId()); assertEquals("SC", f.getUf());
        f.setId(2L); assertThrows(RegraNegocioException.class, () -> new FazendaService(repo).salvar(f));
    }

    @Test void racaDuplicadaNaoEhSalva() {
        RacaRepository repo = mock(RacaRepository.class);
        Raca existente = new Raca(); existente.setId(1L); existente.setNome("Nelore");
        when(repo.findByNomeIgnoreCase("nelore")).thenReturn(Optional.of(existente));
        Raca nova = new Raca(); nova.setNome(" nelore ");
        assertThrows(RegraNegocioException.class, () -> new RacaService(repo).salvar(nova));
        verify(repo, never()).save(any());
    }

    @Test void alimentacaoExigeQuantidadeEUnidadeJuntas() {
        Alimentacao a = new Alimentacao(); a.setData(hoje); a.setDescricao("Racao"); a.setQuantidade(BigDecimal.ONE);
        assertThrows(RegraNegocioException.class, () -> new AlimentacaoService(mock(AlimentacaoRepository.class), referencias).salvar(a));
    }

    @Test void alimentacaoGeralDispensaAnimal() {
        Alimentacao a = new Alimentacao(); a.setData(hoje); a.setDescricao("Racao");
        AlimentacaoRepository repo = mock(AlimentacaoRepository.class);
        new AlimentacaoService(repo, referencias).salvar(a);
        verify(repo).save(a);
    }

    @Test void despesaPodeSerRegistradaDepoisDaVenda() {
        Animal a = animal(1, Sexo.MACHO); a.setSituacao(StatusAnimal.VENDIDO);
        Despesa d = new Despesa(); d.setAnimal(a); d.setData(hoje); d.setCategoria(CategoriaDespesa.OUTRA);
        d.setDescricao("Acerto de transporte"); d.setValor(BigDecimal.TEN);
        DespesaRepository repo = mock(DespesaRepository.class);
        new DespesaService(repo, referencias).salvar(d); verify(repo).save(d);
    }

    @Test void pesagemNaoAceitaPesoZero() {
        Pesagem p = new Pesagem(); p.setAnimal(animal(1, Sexo.MACHO)); p.setDataHora(LocalDateTime.now().minusHours(1)); p.setPesoKg(BigDecimal.ZERO);
        assertThrows(RegraNegocioException.class, () -> new PesagemService(mock(PesagemRepository.class), referencias).salvar(p));
    }

    @Test void pesagemNaoAceitaFuturo() {
        Pesagem p = new Pesagem(); p.setAnimal(animal(1, Sexo.MACHO)); p.setDataHora(LocalDateTime.now().plusDays(1)); p.setPesoKg(BigDecimal.TEN);
        assertThrows(RegraNegocioException.class, () -> new PesagemService(mock(PesagemRepository.class), referencias).salvar(p));
    }

    @Test void sanitarioNaoAceitaProximaAplicacaoAnterior() {
        RegistroSanitario r = new RegistroSanitario(); r.setAnimal(animal(1, Sexo.FEMEA)); r.setTipo(TipoSanitario.VACINA);
        r.setDataAplicacao(hoje); r.setDescricao("Vacina"); r.setProximaAplicacao(hoje.minusDays(1));
        assertThrows(RegraNegocioException.class, () -> new RegistroSanitarioService(mock(RegistroSanitarioRepository.class), referencias).salvar(r));
    }

    @Test void sanitarioExigeUnidadeDaDose() {
        RegistroSanitario r = new RegistroSanitario(); r.setAnimal(animal(1, Sexo.FEMEA)); r.setTipo(TipoSanitario.VACINA);
        r.setDataAplicacao(hoje); r.setDescricao("Vacina"); r.setDose(BigDecimal.ONE);
        assertThrows(RegraNegocioException.class, () -> new RegistroSanitarioService(mock(RegistroSanitarioRepository.class), referencias).salvar(r));
    }

    private Reproducao reproducao() {
        Reproducao r = new Reproducao(); r.setMatriz(animal(1, Sexo.FEMEA)); r.setTipo(TipoReproducao.INSEMINACAO);
        r.setDataEvento(hoje.minusDays(10)); return r;
    }

    @Test void reproducaoExigeMatrizFemea() {
        Reproducao r = reproducao(); r.getMatriz().setSexo(Sexo.MACHO);
        assertThrows(RegraNegocioException.class, () -> new ReproducaoService(mock(ReproducaoRepository.class), referencias).salvar(r));
    }

    @Test void partoExigeDataDeDesfecho() {
        Reproducao r = reproducao(); r.setResultado(ResultadoReproducao.PARTO);
        assertThrows(RegraNegocioException.class, () -> new ReproducaoService(mock(ReproducaoRepository.class), referencias).salvar(r));
    }

    @Test void reprodutorExternoECadastradoSaoExclusivos() {
        Reproducao r = reproducao(); r.setReprodutor(animal(2, Sexo.MACHO)); r.setReferenciaReprodutorExterno("Externo");
        assertThrows(RegraNegocioException.class, () -> new ReproducaoService(mock(ReproducaoRepository.class), referencias).salvar(r));
    }

    @Test void senhaEhCodificadaEEmailNormalizado() {
        UsuarioRepository repo = mock(UsuarioRepository.class); when(repo.save(any())).thenAnswer(i -> i.getArgument(0));
        Usuario u = new UsuarioService(repo).salvar(new UsuarioRequest("Dono", " DONO@EXEMPLO.COM ", "senha-teste"));
        assertEquals("dono@exemplo.com", u.getEmail()); assertNotEquals("senha-teste", u.getSenhaHash());
        assertTrue(new BCryptPasswordEncoder().matches("senha-teste", u.getSenhaHash()));
    }

    @Test void atualizarUsuarioSemSenhaPreservaHash() {
        UsuarioRepository repo = mock(UsuarioRepository.class); Usuario u = new Usuario(); u.setId(1L); u.setSenhaHash("hash-existente");
        when(repo.findById(1L)).thenReturn(Optional.of(u)); when(repo.save(any())).thenAnswer(i -> i.getArgument(0));
        new UsuarioService(repo).atualizar(1L, new UsuarioRequest("Dono", "dono@exemplo.com", null));
        assertEquals("hash-existente", u.getSenhaHash());
    }

    @Test void emailComEspacosEhInvalido() {
        assertThrows(RegraNegocioException.class, () -> new UsuarioService(mock(UsuarioRepository.class)).salvar(new UsuarioRequest("Dono", "d ono@exemplo.com", "senha")));
    }

    @Test void naoPermiteItemAvulso() {
        assertThrows(RegraNegocioException.class, () -> new ItemVendaService(mock(ItemVendaRepository.class)).salvar(new Itemvenda()));
    }

    @Test void morteAtualizaSituacaoENaoPermiteRetorno() {
        Animal a = animal(1, Sexo.MACHO);
        MovimentacaoService service = new MovimentacaoService(movimentos, referencias, animais, mock(HistoricoAnimal.class));
        Movimentacao m = new Movimentacao(); m.setAnimal(a); m.setData(hoje); m.setTipo(TipoMovimentacao.SAIDA); m.setMotivo(MotivoMovimentacao.MORTE);
        service.salvar(m); assertEquals(StatusAnimal.MORTO, a.getSituacao());
        Movimentacao retorno = new Movimentacao(); retorno.setAnimal(a); retorno.setData(hoje); retorno.setTipo(TipoMovimentacao.ENTRADA); retorno.setMotivo(MotivoMovimentacao.COMPRA);
        assertThrows(RegraNegocioException.class, () -> service.salvar(retorno));
    }

    @Test void saidaPorVendaExigeServicoDeVenda() {
        Movimentacao m = new Movimentacao(); m.setTipo(TipoMovimentacao.SAIDA); m.setMotivo(MotivoMovimentacao.VENDA);
        assertThrows(RegraNegocioException.class, () -> new MovimentacaoService(movimentos, referencias, animais, mock(HistoricoAnimal.class)).salvar(m));
    }

    @Test void saidaNaoPodePrecederManejoJaRegistrado() {
        PesagemRepository pesagens = mock(PesagemRepository.class);
        when(pesagens.existsByAnimalIdAndDataHoraAfter(eq(1L), any())).thenReturn(true);
        HistoricoAnimal historico = new HistoricoAnimal(mock(AlimentacaoRepository.class), pesagens, mock(RegistroSanitarioRepository.class), mock(ReproducaoRepository.class));
        assertThrows(RegraNegocioException.class, () -> historico.validarSaida(1L, hoje.minusDays(1)));
    }

    @Test void entradaInicialNaoPodeSerPosteriorAoManejo() {
        AlimentacaoRepository alimentacoes = mock(AlimentacaoRepository.class);
        when(alimentacoes.existsByAnimalIdAndDataBefore(1L, hoje)).thenReturn(true);
        HistoricoAnimal historico = new HistoricoAnimal(alimentacoes, mock(PesagemRepository.class), mock(RegistroSanitarioRepository.class), mock(ReproducaoRepository.class));
        assertThrows(RegraNegocioException.class, () -> historico.validarEntradaInicial(1L, hoje));
    }
}
