package com.example.lms_system_api.config;

import com.example.lms_system_api.dto.UserDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class KCJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final KCRoleConverter roleConverter = new KCRoleConverter();


    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = Optional.ofNullable(roleConverter.convert(jwt))
                .orElseGet(List::of);

        UserDto userDto = new UserDto(
                jwt.getClaimAsString("sub"),
                jwt.getClaimAsString("preferred_username"),
                jwt.getClaimAsString("email"),
                jwt.getClaimAsString("given_name"),
                jwt.getClaimAsString("family_name")
        );

        return new UsernamePasswordAuthenticationToken(
                userDto,
                jwt,
                authorities
        );
    }
}
