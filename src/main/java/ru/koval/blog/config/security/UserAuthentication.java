package ru.koval.blog.config.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashSet;

public class UserAuthentication implements Authentication {
    private final Collection<? extends GrantedAuthority> authorities;
    private boolean authenticated;
    private final UserPrincipal userPrincipal;

    @JsonCreator
    public UserAuthentication(@JsonProperty("userPrincipal") UserPrincipal userPrincipal, @JsonProperty("authenticated") Boolean authenticated, @JsonProperty("authorities") Collection<? extends GrantedAuthority> authorities) {
        this.userPrincipal = userPrincipal;
        this.authenticated = authenticated;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new HashSet<>(authorities);
    }

    @Override
    @JsonIgnore
    public Object getCredentials() {
        return null;
    }

    @Override
    @JsonIgnore
    public Object getDetails() {
        return null;
    }

    @Override
    @JsonIgnore
    public UserPrincipal getPrincipal() {
        return userPrincipal;
    }

    @Override
    public boolean isAuthenticated() {
        return this.authenticated;
    }

    @Override
    public void setAuthenticated(boolean authenticated) throws IllegalArgumentException {
        this.authenticated = authenticated;
    }

    @Override
    @JsonIgnore
    public String getName() {
        return userPrincipal.getLogin();
    }
}
