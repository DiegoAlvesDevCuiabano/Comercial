package com.controle_comercial.service;

import com.controle_comercial.model.entity.Usuario;
import com.controle_comercial.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Logger logger = LoggerFactory.getLogger(UsuarioDetailsService.class);

        logger.info("Iniciando o processo de autenticação para o usuário: {}", username);

        // Busca o usuário no repositório
        Usuario usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> {
                    logger.error("Usuário não encontrado: {}", username);
                    return new UsernameNotFoundException("Usuário não encontrado: " + username);
                });

        logger.info("Usuário encontrado no banco de dados: {}", usuario.getUsuario());
        logger.debug("Status do usuário: {}", usuario.getStatus());
        logger.debug("Hash da senha do usuário: {}", usuario.getSenha());

        // Construção do UserDetails
        UserDetails userDetails = User.builder()
                .username(usuario.getUsuario())
                .password(usuario.getSenha())
                .authorities("ROLE_" + usuario.getStatus().toUpperCase()) // Ex: "ROLE_MASTER"
                .build();



        logger.info("UserDetails criado com sucesso para o usuário: {}", username);

        return userDetails;
    }
}
