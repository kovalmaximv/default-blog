package ru.koval.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.koval.blog.dto.api.LoginRequest;
import ru.koval.blog.dto.api.RefreshRequest;
import ru.koval.blog.dto.api.TokenResponse;
import ru.koval.blog.service.SignService;

@Controller
@Validated
public class SignController {

    private final SignService signService;

    @Autowired
    public SignController(SignService signService) {
        this.signService = signService;
    }


    @PostMapping(path = "/api/v1/registration")
    public ResponseEntity<TokenResponse> registration(@RequestBody LoginRequest loginRequest) {
        TokenResponse tokenResponse = signService.registration(loginRequest);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenResponse.getAccessToken())
                .body(tokenResponse);
    }


    @PostMapping(path = "/api/v1/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest) {
        TokenResponse tokenResponse = signService.login(loginRequest);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenResponse.getAccessToken())
                .body(tokenResponse);
    }


    @PostMapping(path = "/api/v1/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody RefreshRequest refreshRequest) {
        TokenResponse tokenResponse = signService.refresh(refreshRequest);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenResponse.getAccessToken())
                .body(tokenResponse);
    }
}
