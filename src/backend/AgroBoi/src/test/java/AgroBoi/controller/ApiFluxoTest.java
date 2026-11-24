package AgroBoi.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import AgroBoi.repository.*;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/** Fluxo ponta a ponta pela API REST (H2 em modo MariaDB, perfil test). */
@SpringBootTest
@ActiveProfiles("test")
class ApiFluxoTest {
    @Autowired WebApplicationContext contexto;
    @Autowired PesagemRepository pesagens;
    @Autowired DespesaRepository despesas;
    @Autowired MovimentacaoRepository movimentacoes;
    @Autowired ItemVendaRepository itensVenda;
    @Autowired VendaRepository vendas;
    @Autowired AnimalRepository animais;
    @Autowired RacaRepository racas;
    @Autowired UsuarioRepository usuarios;
    MockMvc mvc;

    @BeforeEach
    void montar() {
        mvc = MockMvcBuilders.webAppContextSetup(contexto).apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }

    // Os outros testes de integração apagam animais; limpamos o que criamos para não deixar FKs pendentes.
    @AfterEach
    void limpar() {
        pesagens.deleteAll();
        despesas.deleteAll();
        movimentacoes.deleteAll();
        itensVenda.deleteAll();
        vendas.deleteAll();
        animais.deleteAll();
        racas.deleteAll();
        usuarios.deleteAll();
    }

    private static String json(String texto) { return texto; }

    @Test
    void semTokenRetorna401() throws Exception {
        mvc.perform(get("/api/animais")).andExpect(status().isUnauthorized());
    }

    @Test
    void loginComSenhaErradaRetorna401() throws Exception {
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(json("{\"email\":\"ninguem@agroboi.com\",\"senha\":\"x\"}")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void usuarioComumNaoAcessaGestaoDeUsuarios() throws Exception {
        String token = cadastrarELogar("comum@agroboi.com");
        mvc.perform(get("/api/usuarios").header("Authorization", "Bearer " + token)).andExpect(status().isForbidden());
    }

    @Test
    void fluxoCompleto() throws Exception {
        String token = cadastrarELogar("produtor@agroboi.com");
        String auth = "Bearer " + token;

        // raça
        String racaJson = mvc.perform(post("/api/racas").header("Authorization", auth).contentType(MediaType.APPLICATION_JSON)
                .content(json("{\"nome\":\"Nelore\"}")))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        int racaId = JsonPath.read(racaJson, "$.id");

        // animal
        String animalJson = mvc.perform(post("/api/animais").header("Authorization", auth).contentType(MediaType.APPLICATION_JSON)
                .content(json("{\"identificacao\":\"b-001\",\"nome\":\"Mimosa\",\"sexo\":\"FEMEA\",\"dataNascimento\":\"2024-01-10\",\"racaId\":" + racaId + ",\"situacao\":\"NA_FAZENDA\"}")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.identificacao").value("B-001"))
                .andExpect(jsonPath("$.racaId").value(racaId))
                .andReturn().getResponse().getContentAsString();
        int animalId = JsonPath.read(animalJson, "$.id");

        // identificação duplicada -> 409
        mvc.perform(post("/api/animais").header("Authorization", auth).contentType(MediaType.APPLICATION_JSON)
                .content(json("{\"identificacao\":\"B-001\",\"sexo\":\"MACHO\"}")))
                .andExpect(status().isConflict());

        // lista, pesquisa por raça e por identificação
        mvc.perform(get("/api/animais").header("Authorization", auth))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/api/animais").param("busca", "nelor").header("Authorization", auth))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/api/animais").param("busca", "zzz").header("Authorization", auth))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));

        // edição
        mvc.perform(put("/api/animais/" + animalId).header("Authorization", auth).contentType(MediaType.APPLICATION_JSON)
                .content(json("{\"identificacao\":\"B-001\",\"nome\":\"Mimosa II\",\"sexo\":\"FEMEA\",\"dataNascimento\":\"2024-01-10\",\"racaId\":" + racaId + ",\"situacao\":\"NA_FAZENDA\"}")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.nome").value("Mimosa II"));

        // exclusão é bloqueada pela regra de negócio (preserva histórico)
        mvc.perform(delete("/api/animais/" + animalId).header("Authorization", auth)).andExpect(status().isBadRequest());

        // 404
        mvc.perform(get("/api/animais/999999").header("Authorization", auth)).andExpect(status().isNotFound());

        // pesagem
        mvc.perform(post("/api/pesagens").header("Authorization", auth).contentType(MediaType.APPLICATION_JSON)
                .content(json("{\"animalId\":" + animalId + ",\"dataHora\":\"2025-06-01T08:00:00\",\"pesoKg\":350.5}")))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.animalId").value(animalId));
        mvc.perform(get("/api/pesagens").param("animalId", String.valueOf(animalId)).header("Authorization", auth))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));

        // despesa sem animal (despesa geral)
        mvc.perform(post("/api/despesas").header("Authorization", auth).contentType(MediaType.APPLICATION_JSON)
                .content(json("{\"categoria\":\"ALIMENTACAO\",\"descricao\":\"Ração\",\"data\":\"2025-06-02\",\"valor\":100.00}")))
                .andExpect(status().isCreated());

        // venda -> animal passa a VENDIDO
        mvc.perform(post("/api/vendas").header("Authorization", auth).contentType(MediaType.APPLICATION_JSON)
                .content(json("{\"data\":\"" + java.time.LocalDate.now() + "\",\"comprador\":\"Frigorífico\",\"itens\":[{\"animalId\":" + animalId + ",\"valor\":4500.00}]}")))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.itens.length()").value(1));
        mvc.perform(get("/api/animais/" + animalId).header("Authorization", auth))
                .andExpect(jsonPath("$.situacao").value("VENDIDO"));

        // dashboard e relatórios
        mvc.perform(get("/api/dashboard").header("Authorization", auth))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalBovinos").value(0)).andExpect(jsonPath("$.vendidos").value(1));
        mvc.perform(get("/api/relatorios/rebanho").header("Authorization", auth)).andExpect(status().isOk());
        mvc.perform(get("/api/relatorios/pesagens").header("Authorization", auth)).andExpect(status().isOk());
        mvc.perform(get("/api/relatorios/financeiro").header("Authorization", auth)).andExpect(status().isOk());
        mvc.perform(get("/api/relatorios/vacinacao").header("Authorization", auth)).andExpect(status().isOk());
        mvc.perform(get("/api/relatorios/reproducao").header("Authorization", auth)).andExpect(status().isOk());
    }

    private String cadastrarELogar(String email) throws Exception {
        mvc.perform(post("/api/auth/cadastro").contentType(MediaType.APPLICATION_JSON)
                .content(json("{\"nome\":\"Teste\",\"email\":\"" + email + "\",\"senha\":\"senha123\"}")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.perfil").value("USUARIO_COMUM"))
                .andExpect(jsonPath("$.senha").doesNotExist())
                .andExpect(jsonPath("$.senhaHash").doesNotExist());
        String resposta = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(json("{\"email\":\"" + email + "\",\"senha\":\"senha123\"}")))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return JsonPath.read(resposta, "$.token");
    }
}
