package com.invitacion.tanque.service;

import com.invitacion.tanque.model.Usuario;
import com.invitacion.tanque.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    private final UsuarioRepository usuarioRepository;

    public UserService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public void aceptarInvitacion(String token) {

        Optional<Usuario> usuarioOpt = usuarioRepository.findByTokenConfirmacion(token);

        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Token inválido.");
        }

        Usuario usuario = usuarioOpt.get();

        if (usuario.getTokenExpiracion() != null && usuario.getTokenExpiracion().isBefore(LocalDateTime.now())) {
            usuario.setTokenConfirmacion(null);
            usuario.setTokenExpiracion(null);
            usuarioRepository.save(usuario);
            throw new RuntimeException("El token ha expirado. Me demoré en hacer la webada, pero el token no espera.");
        }

        if (usuario.getAsistencia()) {
            throw new RuntimeException("Ya habías confirmado, maldito idiota. ¡Relájate!");
        }

        usuario.setAsistencia(true);
        usuario.setTokenConfirmacion(null);
        usuario.setTokenExpiracion(null);

        usuarioRepository.save(usuario);
    }
}
