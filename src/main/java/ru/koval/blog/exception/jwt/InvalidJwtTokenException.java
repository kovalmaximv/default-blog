package ru.koval.blog.exception.jwt;

import org.springframework.security.core.AuthenticationException;

public class InvalidJwtTokenException extends AuthenticationException {
    public InvalidJwtTokenException() {
        super("Invalid JWT token");
    }
}
