package com.example.lms_system_api.service;

import com.example.lms_system_api.dto.KeycloakTokenResponse;
import com.example.lms_system_api.dto.LoginRequestDto;
import com.example.lms_system_api.dto.TokenResponse;
import com.example.lms_system_api.exception.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final RestTemplate restTemplate;

    @Value("${keycloak.token-uri}")
    private String tokenUri;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.resource-server.jwt.issuer-uri}")
    private String issuerUri;


    private TokenResponse map(KeycloakTokenResponse kc) {
        TokenResponse response = new TokenResponse();
        response.setAccessToken(kc.getAccessToken());
        response.setRefreshToken(kc.getRefreshToken());
        response.setExpiresIn(kc.getExpiresIn());
        response.setRefreshExpiresIn(kc.getRefreshExpiresIn());
        return response;
    }

    public TokenResponse login(LoginRequestDto request) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("username", request.getUsername());
        form.add("password", request.getPassword());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> entity =
                new HttpEntity<>(form, headers);

        ResponseEntity<KeycloakTokenResponse> response =
                restTemplate.postForEntity(
                        tokenUri,
                        entity,
                        KeycloakTokenResponse.class
                );

        KeycloakTokenResponse body = response.getBody();

        if (body == null) {
            throw new AuthException("Keycloak token response is empty!");
        }

        return map(body);
    }

    public TokenResponse refresh(String refreshToken) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "refresh_token");
        form.add("client_secret", clientSecret);
        form.add("client_id", clientId);
        form.add("refresh_token", refreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);

        ResponseEntity<KeycloakTokenResponse> response = restTemplate.postForEntity(
                tokenUri,
                entity,
                KeycloakTokenResponse.class
        );
        KeycloakTokenResponse body = response.getBody();

        if (body == null) {
            throw new AuthException("Keycloak token response in empty!");
        }
        return map(body);
    }

    public boolean validatePassword(String username, String password) {
        try {
            String url = issuerUri + "/protocol/openid-connect/token";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("grant_type", "password");
            map.add("client_id", clientId);
            map.add("client_secret", clientSecret);
            map.add("username", username);
            map.add("password", password);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

            restTemplate.postForEntity(url, request, Map.class);

            return true;
        } catch (HttpClientErrorException.Unauthorized e) {
            return false;
        } catch (Exception e) {
            throw new AuthException("Keycloak connection error");
        }
    }
}
