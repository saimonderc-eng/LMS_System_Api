package com.example.lms_system_api.service;

import com.example.lms_system_api.dto.KeycloakTokenResponse;
import com.example.lms_system_api.dto.LoginRequestDto;
import com.example.lms_system_api.dto.TokenResponse;
import com.example.lms_system_api.exception.LmsAuthException;
import com.example.lms_system_api.exception.LmsBadRequestException;
import com.example.lms_system_api.exception.LmsInternalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;


@Service
@Slf4j
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
        log.info("Requested to login by username: {}", request.getUsername());

        try {
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
                log.error("Error keycloak token is empty!");
                throw new LmsBadRequestException("Keycloak token response is empty!");
            }
            log.info("Successfully login to account by username: {}", request.getUsername());
            return map(body);
        } catch (HttpClientErrorException ex) {
            log.warn("Keycloak rejected login for {}: {}", request.getUsername(), ex.getStatusCode());
            throw new LmsAuthException("Invalid credentials!");
        }
    }

    public TokenResponse refresh(String refreshToken) {
        log.info("Requested to refresh jwt tokens");

        try {
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
                log.error("Your keycloak token is empty!");
                throw new LmsBadRequestException("Keycloak token response in empty!");
            }
            log.info("Successfully refreshed tokens!");
            return map(body);
        } catch (HttpClientErrorException.BadRequest ex) {
            log.warn("Invalid refresh token request: {}", ex.getResponseBodyAsString());
            throw new LmsBadRequestException("Refresh token is invalid or already used!");
        }
    }

    public boolean validatePassword(String username, String password) {
        log.info("Requested to validate password by username: {}", username);
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

            log.info("Successfully validated password for user: {}!", username);
            return true;


        } catch (HttpClientErrorException.Unauthorized | HttpClientErrorException.BadRequest ex) {
            log.warn("Invalid credentials for user: {}!", username);
            return false;
        } catch (HttpServerErrorException ex) {
            log.error("Keycloak error during validating password for user: {}, {}!", username, ex.getMessage());
            throw new LmsAuthException("Couldn't reach out keycloak!");
        } catch (Exception ex) {
            log.error("Unexpected error occurred during password validating!");
            throw new LmsInternalException("Internal validating error!");
        }
    }
}