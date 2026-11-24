package AgroBoi.security;

import AgroBoi.domain.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Optional;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Gera e valida os tokens JWT (HS256/HS512, conforme o tamanho da chave). */
@Component
public class JwtService {
    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    private final SecretKey chave;
    private final long expiracaoMs;

    public JwtService(@Value("${agroboi.jwt.secret:}") String segredo,
                      @Value("${agroboi.jwt.expiracao-minutos:480}") long expiracaoMinutos) {
        byte[] bytes;
        if (segredo == null || segredo.isBlank()) {
            // Sem JWT_SECRET definido: chave aleatória; os tokens deixam de valer ao reiniciar.
            bytes = new byte[64];
            new SecureRandom().nextBytes(bytes);
            log.warn("JWT_SECRET não definido: usando chave aleatória temporária. Os logins serão invalidados a cada reinicialização.");
        } else {
            bytes = segredo.getBytes(StandardCharsets.UTF_8);
            if (bytes.length < 32) {
                throw new IllegalStateException("JWT_SECRET precisa ter pelo menos 32 caracteres.");
            }
        }
        this.chave = Keys.hmacShaKeyFor(bytes);
        this.expiracaoMs = expiracaoMinutos * 60_000L;
    }

    public String gerar(Usuario usuario) {
        Date agora = new Date();
        return Jwts.builder()
                .subject(usuario.getEmail())
                .claim("perfil", usuario.getPerfil())
                .issuedAt(agora)
                .expiration(new Date(agora.getTime() + expiracaoMs))
                .signWith(chave)
                .compact();
    }

    public long getExpiracaoSegundos() {
        return expiracaoMs / 1000L;
    }

    /** Retorna o e-mail (subject) se o token for válido e não estiver expirado. */
    public Optional<String> emailDoToken(String token) {
        try {
            Claims claims = Jwts.parser().verifyWith(chave).build().parseSignedClaims(token).getPayload();
            return Optional.ofNullable(claims.getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
