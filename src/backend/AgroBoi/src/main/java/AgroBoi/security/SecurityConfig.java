package AgroBoi.security;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * API stateless com JWT. Públicos: login e documentação.
 * Operações da fazenda exigem ADMINISTRADOR. Cadastro pela API desabilitado.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtService jwtService, UsuarioDetailsService detalhes)
            throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(sessao -> sessao.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(erro -> erro.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/api/auth/cadastro", "/api/auth/cadastro/**").denyAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auth/me").authenticated()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/api/**").hasRole("ADMINISTRADOR")
                        .anyRequest().denyAll())
                .addFilterBefore(new JwtAuthFilter(jwtService, detalhes), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /** BCrypt, o mesmo algoritmo que o UsuarioService usa para gerar o hash. */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** Origens HTTP/HTTPS explicitas; sirva o frontend por HTTP, não por file://. */
    @Bean
    public CorsConfigurationSource corsConfigurationSource(@Value("${agroboi.cors.origens}") String[] origens) {
        CorsConfiguration config = new CorsConfiguration();
        List<String> permitidas = java.util.Arrays.stream(origens).map(String::trim).toList();
        if (permitidas.isEmpty()) throw new IllegalStateException("Defina ao menos uma origem em CORS_ORIGENS.");
        for (String origem : permitidas) {
            try {
                java.net.URI uri = java.net.URI.create(origem);
                if (!("http".equals(uri.getScheme()) || "https".equals(uri.getScheme()))
                        || uri.getHost() == null || uri.getRawUserInfo() != null
                        || uri.getRawQuery() != null || uri.getRawFragment() != null
                        || (uri.getRawPath() != null && !uri.getRawPath().isEmpty()) || origem.contains("*")) {
                    throw new IllegalArgumentException();
                }
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException("CORS_ORIGENS deve conter origens HTTP/HTTPS sem caminho, barra final ou curinga.");
            }
        }
        config.setAllowedOrigins(permitidas);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource fonte = new UrlBasedCorsConfigurationSource();
        fonte.registerCorsConfiguration("/**", config);
        return fonte;
    }
}
