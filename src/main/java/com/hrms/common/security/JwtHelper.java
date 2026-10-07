package com.hrms.common.security;

import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtHelper {

    public static final long JWT_TOKEN_VALIDITY = 7 * 24 * 60 * 60;
    public static final long REFRESH_TOKEN_VALIDITY = 7 * 24 * 60 * 60;

    private static final String secret = "1KCrT4BFo9EMUNJjQ0y8VswrKFSJmIHp1jZJVP1IU5999EOqb3E1gmNpf5FzYXIZrwpPDHLhRcORigN84ftPfuOt2Q2IKTmRfJP5RRhRCfJJ2wJ4vlMK70fWFeIT5QBE";

    // ✅ Build the signing key once — required by 0.11.5
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private EmployeeRepository employeeRepository;

    public Employee getEmployeeFromToken(String token) {
        String email = getClaimFromToken(token, Claims::getSubject);
        return employeeRepository.findByEmail(email).orElse(null);
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    // ✅ KEY FIX: parserBuilder() + setSigningKey(Key) + build()
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public long getExpirationTime(String token) {
        return getExpirationDateFromToken(token).getTime();
    }

    private Boolean isTokenExpired(String token) {
        return getExpirationDateFromToken(token).before(new Date());
    }

    public String generateAccessToken(Employee employee) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("employeeId", employee.getId());
        claims.put("name", employee.getFirstName()+employee.getLastName());
        claims.put("roleId", employee.getRole() != null ? employee.getRole().getId() : null);
        claims.put("roleName", employee.getRole() != null ? employee.getRole().getName() : null);
        return doGenerateToken(claims, employee.getEmail(), JWT_TOKEN_VALIDITY);
    }

    public String generateRefreshToken(Employee employee) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("employeeId", employee.getId());
        return doGenerateToken(claims, employee.getEmail(), REFRESH_TOKEN_VALIDITY);
    }

    // ✅ KEY FIX: signWith(Key, algorithm) instead of signWith(algorithm, String)
    private String doGenerateToken(Map<String, Object> claims, String subject, long validity) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + validity * 1000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = getUsernameFromToken(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getTokenFromHeader() {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or missing token");
        }
        return authHeader.substring(7);
    }

    public TokenWithExpiry generateAccessTokenWithExpiry(Employee employee) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("employeeId", employee.getId());
        claims.put("name", employee.getFirstName()+employee.getLastName());
        claims.put("roleId", employee.getRole() != null ? employee.getRole().getId() : null);
        claims.put("roleName", employee.getRole() != null ? employee.getRole().getName() : null);

        long now = System.currentTimeMillis();
        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(employee.getEmail())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + JWT_TOKEN_VALIDITY * 1000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();

        return new TokenWithExpiry(token, now + JWT_TOKEN_VALIDITY * 1000);
    }

    public TokenWithExpiry generateRefreshTokenWithExpiry(Employee employee) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("employeeId", employee.getId());

        long now = System.currentTimeMillis();
        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(employee.getEmail())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + REFRESH_TOKEN_VALIDITY * 1000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();

        return new TokenWithExpiry(token, now + REFRESH_TOKEN_VALIDITY * 1000);
    }
}
