package AgroBoi.security;

import AgroBoi.domain.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Gera e valida os tokens JWT (HS256/HS512, conforme o tamanho da chave). */
@Component
public class JwtService {

    private final SecretKey chave;
    private final long expiracaoMs;

    public JwtService(@Value("${agroboi.jwt.secret:}") String segredo,
                      @Value("${agroboi.jwt.expiracao-minutos:480}") long expiracaoMinutos) {
        if (segredo == null || segredo.isBlank()) {
            throw new IllegalStateException("Defina JWT_SECRET no ambiente de execução.");
        }
        byte[] bytes = segredo.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("JWT_SECRET precisa ter pelo menos 32 bytes em UTF-8.");
        }
        if (expiracaoMinutos <= 0 || expiracaoMinutos > Long.MAX_VALUE / 60_000L) {
            throw new IllegalStateException("JWT_EXPIRACAO_MINUTOS deve ser um número positivo válido.");
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
