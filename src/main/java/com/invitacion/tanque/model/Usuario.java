package com.invitacion.tanque.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nombre;

    @Column(unique = true, nullable = false)
    private String correo;

    private Boolean asistencia = false;

    @Column(unique = true, name = "tokenconfirmacion")
    private String tokenConfirmacion;

    @Column(name = "tokenexpiracion")
    private LocalDateTime tokenExpiracion;
}
