package br.com.fiap.techchallenge.hospital.shared.security;

import br.com.fiap.techchallenge.hospital.shared.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JwtService {

    private final JwtProperties properties;
    private final Key signKey;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
        this.signKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(properties.secret()));
    }

    public String generateToken(JwtAuthenticatedUser user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + properties.expirationMs());

        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaims.ROLE, user.role().name());
        if (user.pacienteId() != null) {
            claims.put(JwtClaims.PACIENTE_ID, user.pacienteId().toString());
        }

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.username())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(signKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public long getExpirationMs() {
        return properties.expirationMs();
    }

    public JwtAuthenticatedUser parse(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        UserRole role = UserRole.valueOf(claims.get(JwtClaims.ROLE, String.class));
        Object pacienteIdRaw = claims.get(JwtClaims.PACIENTE_ID);
        UUID pacienteId = pacienteIdRaw == null ? null : UUID.fromString(pacienteIdRaw.toString());

        return new JwtAuthenticatedUser(claims.getSubject(), role, pacienteId);
    }
}
