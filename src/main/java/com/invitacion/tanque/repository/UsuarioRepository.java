package com.invitacion.tanque.repository;

import com.invitacion.tanque.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByTokenConfirmacion(String tokenConfirmacion);
    List<Usuario> findByAsistenciaFalse();
}
