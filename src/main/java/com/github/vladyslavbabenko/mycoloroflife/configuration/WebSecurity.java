package com.github.vladyslavbabenko.mycoloroflife.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurity {
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/registration", "/login").not().fullyAuthenticated()
                .antMatchers("/me", "/me/**").hasRole("USER")
                .antMatchers("/article/new", "/article/**/edit").hasAnyRole("AUTHOR", "ADMIN")
                .antMatchers("/", "/article", "/article/**", "/resources/**").permitAll()
                .anyRequest().authenticated()
                .and().formLogin().loginPage("/login").loginProcessingUrl("/login").defaultSuccessUrl("/")
                .and().logout().logoutSuccessUrl("/").permitAll()
                .and().exceptionHandling().accessDeniedPage("/access-denied");

        return http.build();
    }
}