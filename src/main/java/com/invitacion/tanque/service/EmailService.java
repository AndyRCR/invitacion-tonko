package com.invitacion.tanque.service;

import com.invitacion.tanque.model.Usuario;
import com.invitacion.tanque.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class EmailService {

    private final UsuarioRepository usuarioRepository;
    private final JavaMailSender mailSender;

    @Value("${backend.base-url}")
    private String baseUrl;

    @Value("${spring.mail.username}")
    private String emailSender;

    public EmailService(UsuarioRepository usuarioRepository, JavaMailSender mailSender) {
        this.usuarioRepository = usuarioRepository;
        this.mailSender = mailSender;
    }

    @Transactional
    public void enviarEmailsConfirmacion() throws Exception {

        List<Usuario> usuarios = usuarioRepository.findByAsistenciaFalse();

        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios pendientes de confirmación para enviar correos.");
            return;
        }

        for (Usuario usuario : usuarios) {

            String token = UUID.randomUUID().toString();
            usuario.setTokenConfirmacion(token);
            usuario.setTokenExpiracion(LocalDateTime.now().plusHours(48));

            usuarioRepository.save(usuario);

            String confirmLink = baseUrl + "/confirmar-asistencia/" + token;

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(usuario.getCorreo());
            helper.setFrom(emailSender);
            helper.setSubject("Invitación pal tonko");

            String htmlContent = buildEmailTemplate(usuario.getNombre(), confirmLink);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            System.out.println("Correo enviado a: " + usuario.getCorreo());
        }
    }

    @Transactional
    public void reenviarInvitacion(Integer userId) throws Exception {

        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));

        if (usuario.getAsistencia()) {
            throw new RuntimeException("El usuario '" + usuario.getNombre() + "' (ID " + userId + ") ya ha confirmado su asistencia. No se envió el correo.");
        }

        String token = UUID.randomUUID().toString();
        usuario.setTokenConfirmacion(token);
        usuario.setTokenExpiracion(LocalDateTime.now().plusHours(48));

        usuarioRepository.save(usuario);

        String confirmLink = baseUrl + "/confirmar-asistencia/" + token;

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(usuario.getCorreo());
        helper.setFrom("tu.correo.personal@gmail.com");
        helper.setSubject("Recordatorio: ¡Invitación - CONFIRMÁ YA! No seas falla. :c");

        String htmlContent = buildEmailTemplate(usuario.getNombre(), confirmLink);
        helper.setText(htmlContent, true);

        mailSender.send(message);
        System.out.println("Correo de recordatorio/re-envío enviado a ID " + userId + ": " + usuario.getCorreo());
    }

    private String buildEmailTemplate(String nombre, String link) {

        final String BACKGROUND_IMAGE_URL = "https://media.latinanoticias.pe/2023/11/MAKANAKY.png";
        final String FALLBACK_BACKGROUND_COLOR = "#211911";
        final String CARD_BACKGROUND_COLOR_RGBA = "rgba(255, 255, 255, 0.8)";
        final String CARD_BACKGROUND_COLOR_FALLBACK = "#F8F7F6";

        String htmlTemplate =
                "<!DOCTYPE html>"
                        + "<html lang=\"en\">"
                        + "<head>"
                        + "    <meta charset=\"utf-8\" />"
                        + "    <meta content=\"width=device-width, initial-scale=1.0\" name=\"viewport\" />"
                        + "    <title>15vo tanque</title>"
                        // Metadatos y fuentes (que se mantienen, aunque la mayoría de CSS moderno no funcionará)
                        + "    <link crossorigin=\"\" href=\"https://fonts.gstatic.com/\" rel=\"preconnect\" />"
                        + "    <link href=\"https://fonts.googleapis.com/css2?family=Newsreader:wght@400;500;700;800&amp;display=swap\" rel=\"stylesheet\" />"

                        // ESTILOS CRÍTICOS INCLUIDOS EN EL HEAD (Mejor práctica para VML)
                        + "    <style type=\"text/css\">"
                        + "        body { margin: 0; padding: 0; }"
                        + "        .container { background-color: " + FALLBACK_BACKGROUND_COLOR + "; }"
                        + "    </style>"

                        // Soporte VML para Outlook
                        + "    "

                        + "</head>"

                        + "<body style='background-color: #f8f7f6; font-family: Newsreader, Arial, sans-serif; margin: 0; padding: 0;'>"

                        // ============== INICIO DEL CONTENEDOR CON IMAGEN DE FONDO ====================

                        + "    <table role='presentation' border='0' cellpadding='0' cellspacing='0' width='100%' style='background-color: " + FALLBACK_BACKGROUND_COLOR + ";'>"
                        + "        <tr>"
                        + "            <td align='center'>"

                        // 1. INICIO DEL VML (Para Outlook)
                        + "                "

                        // 2. INICIO DEL CONTENEDOR CENTRAL (Fallback CSS para otros clientes)
                        + "                <div style=\"width: 100%; max-width: 500px; margin: 0 auto; "
                        + "                               background-image: url('" + BACKGROUND_IMAGE_URL + "'); "
                        + "                               background-size: cover; "
                        + "                               background-color: " + FALLBACK_BACKGROUND_COLOR + "; "
                        + "                               background-position: center center; "
                        + "                               padding: 40px 0; color: white;\">"

                        // 3. CONTENIDO DEL EMAIL (El rectángulo blanco/claro con el texto)
                        + "                    <table role='presentation' border='0' cellpadding='0' cellspacing='0' width='90%' align='center' style='"
                        // Declaración para Outlook (usa solo el color sólido)
                        + "                               background-color: " + CARD_BACKGROUND_COLOR_FALLBACK + ";"
                        // Declaración para todos los demás (incluido Gmail)
                        // Usamos el color RGBa. Si Gmail no puede leer la línea anterior, solo usa esta.
                        + "                               background-color: " + CARD_BACKGROUND_COLOR_RGBA + ";"
                        + "                               border-radius: 8px; "
                        + "                               padding: 30px; "
                        + "                               border: 1px solid #ccc; "
                        + "                               box-shadow: 0 5px 10px rgba(0,0,0,0.2); "
                        + "                               mso-table-lspace: 0pt; mso-table-rspace: 0pt;"
                        + "                           '>"
                        + "                        <tr>"
                        + "                            <td align='center' style='padding: 20px 0;'>"

                        // INICIO DEL TEXTO
                        + "                                <h1 style='font-family: Newsreader; font-size: 30px; font-weight: bold; color: #c9731d; margin-top: 0;'>¡Hola " + nombre + "! Estás invitado</h1>"
                        + "                                <p style='margin-top: 25px; text-align: center; color: #333333;'>Acompañanos a la ceremonia del décimo cuarto tanque.</p>"
                        + "                                <p style='color: #333333;'>Estaré muy agradecido de que puedas asistir (a menos de que seas Chamo o Giu).</p>"
                        + "                                <p style='font-weight: bold; margin-top: 15px; color: #333333;'>Miercoles, 8 de Octubre, 2025<br />a las 2:00 PM</p>"
                        + "                                <p style='font-weight: bold; margin-top: 10px; color: #333333;'>Lugar de encuentro: <br />Real Plaza VMT</p>"

                        // BOTÓN DE CONFIRMACIÓN
                        + "                                <div style='margin-top: 35px; text-align: center;'>"
                        + "                                    <label style='display: block; font-size: 14px; font-weight: 500; color: #333333; margin-bottom: 8px;'>Por favor, confirma tu asistencia:</label>"
                        + "                                    <a href=\"" + link + "\" style=\"display: inline-block; width: 80%; background-color: #c9731d; color: white; font-weight: bold; padding: 12px 24px; border-radius: 8px; text-decoration: none; text-align: center; cursor: pointer; mso-padding-alt:0;\">"
                        + "                                        <span style=\"mso-text-raise:10pt;font-weight:bold;\">Confirmo que voy y soy tremendo maricón</span>"
                        + "                                    </a>"
                        + "                                </div>"

                        // FOOTER
                        + "                                <div style='margin-top: 30px; text-align: center; font-size: 10px; color: #333333;'>"
                        + "                                    <p>Esta invitación vence en 48hrs.</p>"
                        + "                                </div>"

                        + "                            </td>"
                        + "                        </tr>"
                        + "                    </table>"

                        // 4. CIERRE DEL CONTENEDOR CENTRAL
                        + "                </div>"

                        // 5. CIERRE DEL VML
                        + "                "

                        + "            </td>"
                        + "        </tr>"
                        + "    </table>"

                        // ============== FIN DEL CONTENEDOR CON IMAGEN DE FONDO ====================

                        + "</body>"
                        + "</html>";

        return htmlTemplate;
    }
}
