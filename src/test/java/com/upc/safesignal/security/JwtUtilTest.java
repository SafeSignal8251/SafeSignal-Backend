package com.upc.safesignal.security;

import com.upc.safesignal.Security.util.JwtUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Pruebas del token JWT (seguridad de la API)
class JwtUtilTest {

    private static final String SECRETO = "bqDqZD1nfCzsxFPcame/Xk7AWoRFjsycSg7NPEQUgS8IuVEg9D7CCBEKjDk6rvaDW4KaUsgu+TOHs7o0ySRLOw==";

    private JwtUtil jwtUtil;
    private UserDetails ana;

    @BeforeEach
    void preparar() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secretKey", SECRETO);
        ana = new User("ana@correo.com", "x", List.of(new SimpleGrantedAuthority("ROLE_CLIENTE")));
    }

    @Test
    void generaUnTokenConElCorreoEnElSubject() {
        String token = jwtUtil.generateToken(ana, 7L);

        assertEquals("ana@correo.com", jwtUtil.extractUsername(token));
        Integer idEnToken = jwtUtil.extractClaim(token, c -> c.get("id_usuario", Integer.class));
        assertEquals(Integer.valueOf(7), idEnToken);
        assertEquals("ROLE_CLIENTE", jwtUtil.extractClaim(token, c -> c.get("role", String.class)));
    }

    @Test
    void validaUnTokenVigenteDelMismoUsuario() {
        assertTrue(jwtUtil.validateToken(jwtUtil.generateToken(ana, 7L), ana));
    }

    @Test
    void rechazaUnTokenDeOtroUsuario() {
        UserDetails otro = new User("otro@correo.com", "x", List.of(new SimpleGrantedAuthority("ROLE_CLIENTE")));

        assertFalse(jwtUtil.validateToken(jwtUtil.generateToken(ana, 7L), otro));
    }

    @Test
    void rechazaUnTokenVencido() {
        String vencido = Jwts.builder().setSubject("ana@correo.com")
                .setIssuedAt(new Date(System.currentTimeMillis() - 7_200_000))
                .setExpiration(new Date(System.currentTimeMillis() - 3_600_000))
                .signWith(SignatureAlgorithm.HS512, SECRETO).compact();

        assertThrows(Exception.class, () -> jwtUtil.validateToken(vencido, ana));
    }

    @Test
    void rechazaUnTokenFirmadoConOtraClave() {
        String falso = Jwts.builder().setSubject("ana@correo.com")
                .setExpiration(new Date(System.currentTimeMillis() + 3_600_000))
                .signWith(SignatureAlgorithm.HS512, "QUJDREVGR0hJSktMTU5PUFFSU1RVVldYWVowMTIzNDU2Nzg5QUJDREVGR0hJSktMTU5PUFFSU1RVVldYWVowMTIzNDU2Nzg5").compact();

        assertThrows(Exception.class, () -> jwtUtil.extractUsername(falso));
    }

    @Test
    void rechazaUnTokenManipulado() {
        String token = jwtUtil.generateToken(ana, 7L);
        String manipulado = token.substring(0, token.length() - 4) + "abcd";

        assertThrows(Exception.class, () -> jwtUtil.extractUsername(manipulado));
    }
}
