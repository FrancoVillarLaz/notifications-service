package proyect3.notifications.notifier.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import proyect3.notifications.exception.NotificationException;
import proyect3.notifications.persistence.entities.Notification;
import proyect3.notifications.persistence.enums.Canal;

/**
 * Estrategia para enviar notificaciones por SMS.
 * Implementación de ejemplo - requiere integración con proveedor (Twilio, AWS SNS, etc.)
 */
@Slf4j
@Component
public class SmsNotificationStrategy implements NotificationStrategy {

    private static final int MAX_SMS_LENGTH = 160;

    @Override
    public void send(Notification notification) {
        validate(notification);

        try {
            // TODO: Integrar con proveedor SMS (Twilio, AWS SNS, etc.)
            log.info("Enviando SMS a: {} - Mensaje: {}",
                    notification.getDestinatarios(),
                    notification.getMensaje());

            // Simulación de envío
            for (String phoneNumber : notification.getDestinatarios()) {
                sendSmsToNumber(phoneNumber, notification.getMensaje());
            }

            log.info("SMS enviado exitosamente a {} destinatarios",
                    notification.getDestinatarios().size());

        } catch (Exception e) {
            log.error("Error al enviar SMS", e);
            throw new NotificationException("Error al enviar SMS", e);
        }
    }

    private void sendSmsToNumber(String phoneNumber, String message) {
        // TODO: Implementar integración real con proveedor SMS
        // Ejemplo con Twilio:
        // twilioClient.messages.create(
        //     new PhoneNumber(phoneNumber),
        //     new PhoneNumber(fromNumber),
        //     message
        // );

        log.debug("SMS simulado enviado a: {}", phoneNumber);
    }

    @Override
    public boolean supports(Canal canal) {
        return Canal.SMS.equals(canal);
    }

    @Override
    public Canal getChannel() {
        return Canal.SMS;
    }

    @Override
    public void validate(Notification notification) {
        if (notification.getDestinatarios() == null || notification.getDestinatarios().isEmpty()) {
            throw new IllegalArgumentException("SMS strategy requires at least one phone number");
        }

        if (notification.getMensaje() == null || notification.getMensaje().isBlank()) {
            throw new IllegalArgumentException("SMS strategy requires a message body");
        }

        // Validar longitud del mensaje
        if (notification.getMensaje().length() > MAX_SMS_LENGTH) {
            log.warn("SMS message exceeds {} characters, may be split into multiple messages",
                    MAX_SMS_LENGTH);
        }

        // Validar formato de números de teléfono (básico)
        for (String phone : notification.getDestinatarios()) {
            if (!phone.matches("^\\+?[1-9]\\d{1,14}$")) {
                throw new IllegalArgumentException("Invalid phone number format: " + phone);
            }
        }
    }
}