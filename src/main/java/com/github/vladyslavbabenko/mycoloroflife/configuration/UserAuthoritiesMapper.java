package com.github.vladyslavbabenko.mycoloroflife.configuration;

import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import com.github.vladyslavbabenko.mycoloroflife.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;

import java.util.HashSet;
import java.util.Set;

public class UserAuthoritiesMapper {

    @Autowired
    private UserService userService;

    public GrantedAuthoritiesMapper authoritiesMapper() {
        return (authorities) -> {
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
            authorities.forEach(authority -> {
                if (authority instanceof OAuth2UserAuthority) {
                    OAuth2UserAuthority oAuth2UserAuthority = (OAuth2UserAuthority) authority;
                    userService.saveOAuth2User(oAuth2UserAuthority);
                    User currentUser =
                            (User) userService.loadUserByUsername((String) oAuth2UserAuthority.getAttributes().get("email"));
                    mappedAuthorities.addAll(currentUser.getRoles());
                }
            });
            return mappedAuthorities;
        };
    }
}