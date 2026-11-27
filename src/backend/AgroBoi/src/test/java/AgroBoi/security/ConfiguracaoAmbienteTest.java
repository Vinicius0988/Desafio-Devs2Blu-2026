package AgroBoi.security;

import AgroBoi.domain.entity.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import static org.junit.jupiter.api.Assertions.*;

class ConfiguracaoAmbienteTest {
    private static final String SEGREDO = "segredo-exclusivo-teste-0123456789abcdef";

    @Test void jwtSemSegredoOuComSegredoCurtoFalha() {
        assertThrows(IllegalStateException.class, () -> new JwtService("", 480));
        assertThrows(IllegalStateException.class, () -> new JwtService("curto", 480));
    }

    @Test void jwtComMesmaChaveSobreviveANovaInstancia() {
        Usuario usuario = new Usuario(); usuario.setEmail("fazendeiro@teste.com");
        String token = new JwtService(SEGREDO, 480).gerar(usuario);
        assertEquals(usuario.getEmail(), new JwtService(SEGREDO, 480).emailDoToken(token).orElseThrow());
        assertTrue(new JwtService(SEGREDO + "outra-chave", 480).emailDoToken(token).isEmpty());
    }

    @Test void expiracaoInvalidaFalha() {
        assertThrows(IllegalStateException.class, () -> new JwtService(SEGREDO, 0));
        assertThrows(IllegalStateException.class, () -> new JwtService(SEGREDO, -1));
        assertThrows(IllegalStateException.class, () -> new JwtService(SEGREDO, Long.MAX_VALUE));
    }

    @Test void corsExigeOrigensExplicitas() {
        SecurityConfig config = new SecurityConfig();
        for (String origem : new String[]{"*", "null", "file://", "https://*.exemplo.com", "http://localhost:5500/", ""}) {
            assertThrows(IllegalStateException.class, () -> config.corsConfigurationSource(new String[]{origem}));
        }
        var source = config.corsConfigurationSource(new String[]{"http://localhost:5500", " https://fazenda.exemplo.com "});
        var cors = source.getCorsConfiguration(new MockHttpServletRequest("GET", "/api/animais"));
        assertNotNull(cors);
        assertEquals("http://localhost:5500", cors.checkOrigin("http://localhost:5500"));
        assertNull(cors.checkOrigin("https://outro.exemplo.com"));
    }

    private ApplicationContextRunner contexto(String... propriedades) {
        return new ApplicationContextRunner()
                .withInitializer(new ConfigDataApplicationContextInitializer())
                .withPropertyValues(propriedades);
    }

    @Test void padraoUsaMariaDbEDesenvolvimento() {
        contexto().run(c -> {
            assertEquals("org.mariadb.jdbc.Driver", c.getEnvironment().getProperty("spring.datasource.driver-class-name"));
            assertEquals("update", c.getEnvironment().getProperty("spring.jpa.hibernate.ddl-auto"));
        });
    }

    @Test void mysqlContinuaDisponivel() {
        contexto("spring.profiles.active=mysql,dev").run(c ->
            assertEquals("com.mysql.cj.jdbc.Driver", c.getEnvironment().getProperty("spring.datasource.driver-class-name")));
    }

    @Test void producaoValidaMesmoComDdlAutoInformado() {
        contexto("spring.profiles.active=mariadb,prod", "DDL_AUTO=create-drop").run(c ->
            assertEquals("validate", c.getEnvironment().getProperty("spring.jpa.hibernate.ddl-auto")));
    }

    @Test void testeIsolaBancoSegredoEAdministrador() {
        contexto("spring.profiles.active=test", "DB_PASSWORD=nao-usar", "ADMIN_EMAIL=nao-usar@teste.com",
                "ADMIN_PASSWORD=nao-usar", "JWT_SECRET=curto").run(c -> {
            var env = c.getEnvironment();
            assertEquals("org.h2.Driver", env.getProperty("spring.datasource.driver-class-name"));
            assertEquals("", env.getProperty("spring.datasource.password"));
            assertEquals("", env.getProperty("agroboi.admin.email"));
            assertEquals("", env.getProperty("agroboi.admin.senha"));
            assertTrue(env.getProperty("agroboi.jwt.secret").length() >= 32);
        });
    }
}
