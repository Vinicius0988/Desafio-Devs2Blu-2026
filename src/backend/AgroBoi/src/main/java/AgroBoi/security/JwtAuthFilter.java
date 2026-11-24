package AgroBoi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Lê "Authorization: Bearer <token>", valida o JWT e autentica a requisição.
 * Não é um @Component de propósito: é criado no SecurityConfig, para rodar só dentro da cadeia do Spring Security.
 */
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UsuarioDetailsService detalhes;

    public JwtAuthFilter(JwtService jwtService, UsuarioDetailsService detalhes) {
        this.jwtService = jwtService;
        this.detalhes = detalhes;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String cabecalho = request.getHeader("Authorization");
        if (cabecalho != null && cabecalho.startsWith("Bearer ")
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            String token = cabecalho.substring(7).trim();
            String email = jwtService.emailDoToken(token).orElse(null);
            if (email != null) {
                try {
                    UserDetails usuario = detalhes.loadUserByUsername(email);
                    if (usuario.isEnabled()) {
                        var autenticacao = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
                        autenticacao.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(autenticacao);
                    }
                } catch (UsernameNotFoundException ignorado) {
                    // Token de um usuário que não existe mais: segue sem autenticação (resultará em 401).
                }
            }
        }
        chain.doFilter(request, response);
    }
}
