package umc.TripPiece.domain.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import umc.TripPiece.apiPayload.code.status.ErrorStatus;
import umc.TripPiece.apiPayload.exception.GeneralException;

@Component
@Slf4j
public class TokenProvider {

    private final JWTProperties jwtProperties;

    public TokenProvider(JWTProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String createToken(String email, long validity) {
        Claims claims = Jwts.claims().setSubject(email);
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + validity);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtProperties.getSecret())
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtProperties.getSecret()).parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
            throw new GeneralException(ErrorStatus.INVALID_TOKEN); // 유효하지 않은 토큰 에러 반환
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
            throw new GeneralException(ErrorStatus.TOKEN_EXPIRED); // 만료된 토큰 에러 반환
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
            throw new GeneralException(ErrorStatus.UNSUPPORTED_TOKEN); // 지원하지 않는 형식 토큰 에러 반환
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
            throw new GeneralException(ErrorStatus.NOT_FOUND_TOKEN); // 토큰의 클레임이 비어 있는 경우 에러 반환
        }
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtProperties.getSecret())
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
}
