package com.example.lms_system_api.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
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

        User user = new User(
                jwt.getClaim("preferred_username"),
                "",
                authorities
        );
        return new UsernamePasswordAuthenticationToken(
          user,
          jwt,
          authorities
        );
    }
}
