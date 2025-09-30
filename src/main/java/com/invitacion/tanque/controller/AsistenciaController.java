package com.invitacion.tanque.controller;

import com.invitacion.tanque.service.EmailService;
import com.invitacion.tanque.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping; // Agregamos un POST para activar el envío de emails (manual)

@Controller
public class AsistenciaController {

    private final UserService userService;
    private final EmailService emailService;

    public AsistenciaController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @GetMapping("/confirmar-asistencia/{token}")
    public String confirmarAsistencia(@PathVariable String token, Model model) {
        try {
            userService.aceptarInvitacion(token);

            model.addAttribute("titulo", "¡Confirmación Exitosa!");
            model.addAttribute("mensaje", "Xvre maldito idiota! No seas falla, me demoré en hacer esta webada :c");
            return "confirmacion_exitosa";

        } catch (RuntimeException e) {

            model.addAttribute("titulo", "Error de Confirmación");
            model.addAttribute("mensaje", "Checa que tu invitacion no haya expirado (48hrs) o quizás ya confirmaste y eres recontra imbecil putamadre.");
            return "confirmacion_fallida";
        }
    }

    @PostMapping("/admin/enviar-invitaciones")
    public String enviarInvitaciones(Model model) {
        try {
            emailService.enviarEmailsConfirmacion();
            model.addAttribute("mensaje", "Emails de invitación enviados con éxito a todos los usuarios pendientes.");
        } catch (Exception e) {
            model.addAttribute("mensaje", "ERROR al enviar emails: " + e.getMessage());
        }
        return "admin_respuesta";
    }

    @PostMapping("/admin/reenviar-invitacion/{userId}")
    public String reenviarInvitacion(@PathVariable Integer userId, Model model) {
        try {
            emailService.reenviarInvitacion(userId);
            model.addAttribute("mensaje", "Invitación reenviada con éxito al usuario ID: " + userId);
        } catch (RuntimeException e) {
            model.addAttribute("mensaje", "ERROR al reenviar invitación: " + e.getMessage());
        } catch (Exception e) {
            model.addAttribute("mensaje", "ERROR de envío: " + e.getMessage());
        }
        return "admin_respuesta";
    }
}
