package proyect3.notifications.notifier;

import proyect3.notifications.exception.EmailSendException;
import proyect3.notifications.persistence.entities.Notification;
import proyect3.notifications.persistence.enums.Canal;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Comment;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Slf4j
@Comment("Clase para enviar notificaciones por correo electrónico")
@Component
public class EmailNotifier implements Notifier {

    private final JavaMailSender mailSender;

    public EmailNotifier(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void send(Notification notification) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("email@correo.com");
            helper.setSubject(notification.getTitulo());
            helper.setTo(notification.getDestinatarios().toArray(new String[0]));

            boolean esHtml = notification.getMensaje().trim().startsWith("<");
            helper.setText(notification.getMensaje(), esHtml);

            if (esHtml && notification.getMetadata() != null) {
                Object cuerpoTexto = notification.getMetadata().get("cuerpoTexto");
                if (cuerpoTexto != null) {
                    helper.setText(cuerpoTexto.toString(), notification.getMensaje());
                }
            }

            mailSender.send(message);

            log.info("Email enviado exitosamente a: {}", notification.getDestinatarios());

        } catch (MessagingException e) {
            log.error("Error al configurar el mensaje de email para: {}", notification.getDestinatarios(), e);
            throw new EmailSendException("Error al configurar el mensaje", e);
        } catch (MailException e) {
            log.error("Error al enviar email a: {}", notification.getDestinatarios(), e);
            throw new EmailSendException("Error al enviar el email", e);
        }
    }

    @Override
    public boolean soporta(Canal canal) {
        return Canal.EMAIL.equals(canal);
    }
}
