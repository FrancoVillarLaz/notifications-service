package proyect3.notifications.notifier.strategy;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import proyect3.notifications.exception.EmailSendException;
import proyect3.notifications.persistence.entities.Notification;
import proyect3.notifications.persistence.enums.Canal;

/**
 * Estrategia para enviar notificaciones por Email.
 * Implementa el patrón Strategy para el canal EMAIL.
 */
@Slf4j
@Component
public class EmailNotificationStrategy implements NotificationStrategy {

    private final JavaMailSender mailSender;
    private static final String FROM_ADDRESS = "sistema@app.inncome.net";

    public EmailNotificationStrategy(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void send(Notification notification) {
        validate(notification);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(FROM_ADDRESS);
            helper.setSubject(notification.getTitulo());
            helper.setTo(notification.getDestinatarios().toArray(new String[0]));

            // Determinar si el mensaje es HTML
            boolean isHtml = notification.getMensaje().trim().startsWith("<");

            if (isHtml && notification.getMetadata() != null
                    && notification.getMetadata().containsKey("cuerpoTexto")) {
                // Si hay versión texto, enviar ambas
                String textContent = notification.getMetadata().get("cuerpoTexto").toString();
                helper.setText(textContent, notification.getMensaje());
            } else {
                helper.setText(notification.getMensaje(), isHtml);
            }

            mailSender.send(message);

            log.info("Email enviado exitosamente a: {} - Template: {}",
                    notification.getDestinatarios(),
                    notification.getMetadata() != null ?
                            notification.getMetadata().get("template") : "N/A");

        } catch (MessagingException e) {
            log.error("Error al configurar el mensaje de email para: {}",
                    notification.getDestinatarios(), e);
            throw new EmailSendException("Error al configurar el mensaje", e);
        } catch (MailException e) {
            log.error("Error al enviar email a: {}", notification.getDestinatarios(), e);
            throw new EmailSendException("Error al enviar el email", e);
        }
    }

    @Override
    public boolean supports(Canal canal) {
        return Canal.EMAIL.equals(canal);
    }

    @Override
    public Canal getChannel() {
        return Canal.EMAIL;
    }

    @Override
    public void validate(Notification notification) {
        if (notification.getDestinatarios() == null || notification.getDestinatarios().isEmpty()) {
            throw new IllegalArgumentException("Email strategy requires at least one recipient");
        }

        if (notification.getTitulo() == null || notification.getTitulo().isBlank()) {
            throw new IllegalArgumentException("Email strategy requires a subject (titulo)");
        }

        if (notification.getMensaje() == null || notification.getMensaje().isBlank()) {
            throw new IllegalArgumentException("Email strategy requires a message body");
        }

        // Validar formato de emails (básico)
        for (String email : notification.getDestinatarios()) {
            if (!email.contains("@")) {
                throw new IllegalArgumentException("Invalid email format: " + email);
            }
        }
    }
}