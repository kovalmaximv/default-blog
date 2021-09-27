package ru.koval.blog.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.koval.blog.config.security.JwtProvider;
import ru.koval.blog.config.security.UserAuthentication;
import ru.koval.blog.config.security.UserPrincipal;
import ru.koval.blog.dto.api.LoginRequest;
import ru.koval.blog.dto.api.RefreshRequest;
import ru.koval.blog.dto.api.TokenResponse;
import ru.koval.blog.exception.UserIncorrectPasswordException;
import ru.koval.blog.exception.jwt.InvalidRefreshTokenException;
import ru.koval.blog.model.User;
import ru.koval.blog.repository.UserRoleRepository;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

@Service
public class SignService {
    private final static Logger logger = LogManager.getLogger(SignService.class);

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final UserRoleRepository userRoleRepository;

    @Autowired
    public SignService(UserService userService, PasswordEncoder passwordEncoder, JwtProvider jwtProvider, UserRoleRepository userRoleRepository) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.userRoleRepository = userRoleRepository;
    }


    @Transactional
    public TokenResponse registration(LoginRequest loginRequest) {
        User user = new User();

        user.setUserRole(userRoleRepository.findByName("ROLE_ADMIN"));
        user.setLogin(loginRequest.getLogin());
        user.setPassword(passwordEncoder.encode(loginRequest.getPassword()));
        userService.save(user);

        UserAuthentication userAuthentication = getAuth(user);

        TokenResponse tokenResponse = jwtProvider.generateTokenResponse(userAuthentication);
        user.setRefreshToken(passwordEncoder.encode(tokenResponse.getRefreshToken()));
        userService.save(user);

        return tokenResponse;
    }


    public TokenResponse login(LoginRequest loginRequest) {
        User user = userService.findByLogin(loginRequest.getLogin());

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new UserIncorrectPasswordException();
        }

        UserAuthentication userAuthentication = getAuth(user);

        TokenResponse tokenResponse = jwtProvider.generateTokenResponse(userAuthentication);
        user.setRefreshToken(passwordEncoder.encode(tokenResponse.getRefreshToken()));
        userService.save(user);

        return tokenResponse;
    }


    public TokenResponse refresh(RefreshRequest refreshRequest) {
        String login = jwtProvider.getUserAuthenticationFromRefreshToken(refreshRequest.getRefreshToken()).getPrincipal().getLogin();

        User user = userService.findByLogin(login);
        if (!passwordEncoder.matches(refreshRequest.getRefreshToken(), user.getRefreshToken())) {
            logger.error("Invalid refresh token {} for user with id {}", refreshRequest.getRefreshToken(), user.getId());
            throw new InvalidRefreshTokenException();
        }

        UserAuthentication userAuthentication = getAuth(user);

        TokenResponse tokenResponse = jwtProvider.generateTokenResponse(userAuthentication);
        user.setRefreshToken(passwordEncoder.encode(tokenResponse.getRefreshToken()));
        userService.save(user);

        return tokenResponse;
    }


    private UserAuthentication getAuth(User user) {
        UserPrincipal userPrincipal = new UserPrincipal();
        userPrincipal.setLogin(user.getLogin());

        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(user.getUserRole().getName()));

        return new UserAuthentication(userPrincipal, true, authorities);
    }

}
