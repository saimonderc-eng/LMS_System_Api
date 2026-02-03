package com.example.lms_system_api.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TokenResponse {

    String accessToken;

    String refreshToken;

    Long expiresIn;

    Long refreshExpiresIn;
}
