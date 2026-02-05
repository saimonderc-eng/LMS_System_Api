package com.example.lms_system_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdatePasswordRequest {

    @NotBlank
    String id;

    @NotBlank
    String currentPassword;

    @NotBlank
    @Size(min = 8, message = "Password must be at least 8 chars long!")
    String newPassword;

    @NotBlank
    String newPasswordConfirmation;
}
