// src/main/java/com/seuprojeto/config/SecurityConfig.java
package com.controle_comercial.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuração de segurança da aplicação.
 * Nesta classe, configuramos:
 * - Regras de autorização para endpoints.
 * - Página customizada para login.
 * - Autenticação com usuário em memória (para testes/inicial).
 * - Configuração de proteção contra CSRF.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login", "/css/**", "/js/**", "/webjars/**", "/static/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")           // Página de login customizada
                        .loginProcessingUrl("/login")  // Endpoint para o POST de autenticação
                        .defaultSuccessUrl("/home", true)  // Redireciona para "/" após sucesso
                        .failureUrl("/login?error")    // Em caso de falha de autenticação
                        .permitAll()                   // Permitir acesso público ao login
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .csrf(Customizer.withDefaults()); // Ativa proteção CSRF

        return http.build();
    }

}


