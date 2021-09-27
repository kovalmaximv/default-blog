package ru.koval.blog.config.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.jsonwebtoken.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import ru.koval.blog.deserializer.GrantedAuthorityDeserializer;
import ru.koval.blog.dto.api.TokenResponse;
import ru.koval.blog.exception.jwt.InvalidJwtTokenException;
import ru.koval.blog.exception.jwt.JwtTokenGenerationException;
import ru.koval.blog.property.JwtProperties;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;


@Component
public class JwtProvider {
    private final static Logger logger = LogManager.getLogger(JwtProvider.class);
    private final static ObjectMapper objectMapper = new ObjectMapper();

    private final JwtProperties jwtProperties;

    @Autowired
    public JwtProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }


    static {
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addAbstractTypeMapping(GrantedAuthority.class, SimpleGrantedAuthority.class);
        simpleModule.addDeserializer(SimpleGrantedAuthority.class, new GrantedAuthorityDeserializer());
        objectMapper.registerModule(simpleModule);
    }


    public TokenResponse generateTokenResponse(UserAuthentication userAuthentication) {
        TokenResponse tokenResponse = new TokenResponse();

        tokenResponse.setAccessToken(generateAccessToken(userAuthentication));
        tokenResponse.setRefreshToken(generateRefreshToken(userAuthentication));
        tokenResponse.setExpiresAt(LocalDateTime.now().plus(jwtProperties.getAccessTokenExpiration()));

        return tokenResponse;
    }


    public UserAuthentication getUserAuthenticationFromAccessToken(String accessToken) {
        return getUserAuthenticationFromToken(accessToken, jwtProperties.getAccessSecret());
    }


    public UserAuthentication getUserAuthenticationFromRefreshToken(String refreshToken) {
        return getUserAuthenticationFromToken(refreshToken, jwtProperties.getRefreshSecret());
    }


    private UserAuthentication getUserAuthenticationFromToken(String token, String secret) {
        try {
            String subjectJson = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody().getSubject();

            return objectMapper.readValue(subjectJson, UserAuthentication.class);
        } catch (ExpiredJwtException expiredJwtException) {
            logger.error("Token expired. {}", expiredJwtException.getMessage());
            throw new InvalidJwtTokenException();
        } catch (UnsupportedJwtException unsupportedJwtException) {
            logger.error("Token format exception. {}", unsupportedJwtException.getMessage());
            throw new InvalidJwtTokenException();
        } catch (SignatureException signatureException) {
            logger.error("Token signature exception. {}", signatureException.getMessage());
            throw new InvalidJwtTokenException();
        } catch (JwtException jwtException) {
            logger.error("Some JWT exception. {}", jwtException.getMessage());
            throw new InvalidJwtTokenException();
        } catch (JsonProcessingException e) {
            logger.error("Jackson exception while trying read json token's subject. {}", e.getMessage());
            throw new InvalidJwtTokenException();
        }
    }


    private String generateAccessToken(UserAuthentication userAuthentication) {
        return generateToken(userAuthentication, jwtProperties.getAccessSecret(), jwtProperties.getAccessTokenExpiration());
    }


    private String generateRefreshToken(UserAuthentication userAuthentication) {
        return generateToken(userAuthentication, jwtProperties.getRefreshSecret(), jwtProperties.getRefreshTokenExpiration());
    }


    private String generateToken(UserAuthentication userAuthentication, String secret, Duration tokenExpiration) {
        Date date = Date.from(
                LocalDateTime.now().plus(tokenExpiration).atZone(ZoneId.systemDefault()).toInstant()
        );

        try {
            return Jwts.builder()
                    .setSubject(objectMapper.writeValueAsString(userAuthentication))
                    .setExpiration(date)
                    .signWith(SignatureAlgorithm.HS512, secret)
                    .compact();
        } catch (JsonProcessingException exception) {
            logger.error("Exception while jackson write userAuth to token subject. {}", exception.getMessage());
            throw new JwtTokenGenerationException();
        }
    }

}
