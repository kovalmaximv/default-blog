package ru.koval.blog.exception.jwt;

import org.springframework.security.core.AuthenticationException;

public class JwtTokenGenerationException extends AuthenticationException {
    public JwtTokenGenerationException() {
        super("Token generation went wrong.");
    }
}
