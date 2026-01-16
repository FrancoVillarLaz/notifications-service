package proyect3.notifications.notifier.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import proyect3.notifications.exception.NotificationException;
import proyect3.notifications.persistence.entities.Notification;
import proyect3.notifications.persistence.enums.Canal;

/**
 * Estrategia para enviar notificaciones por WhatsApp.
 * Implementación de ejemplo - requiere integración con WhatsApp Business API.
 */
@Slf4j
@Component
public class WhatsAppNotificationStrategy implements NotificationStrategy {

    @Override
    public void send(Notification notification) {
        validate(notification);

        try {
            // TODO: Integrar con WhatsApp Business API
            log.info("Enviando WhatsApp a: {} - Mensaje: {}",
                    notification.getDestinatarios(),
                    notification.getMensaje());

            for (String phoneNumber : notification.getDestinatarios()) {
                sendWhatsAppMessage(phoneNumber, notification);
            }

            log.info("WhatsApp enviado exitosamente a {} destinatarios",
                    notification.getDestinatarios().size());

        } catch (Exception e) {
            log.error("Error al enviar WhatsApp", e);
            throw new NotificationException("Error al enviar WhatsApp", e);
        }
    }

    private void sendWhatsAppMessage(String phoneNumber, Notification notification) {
        // TODO: Implementar integración real con WhatsApp Business API
        // Ejemplo con Twilio WhatsApp:
        // twilioClient.messages.create(
        //     new PhoneNumber("whatsapp:" + phoneNumber),
        //     new PhoneNumber("whatsapp:" + fromNumber),
        //     notification.getMensaje()
        // );

        // Si hay template ID en metadata, usar template de WhatsApp
        if (notification.getMetadata() != null
                && notification.getMetadata().containsKey("whatsappTemplateId")) {
            String templateId = notification.getMetadata().get("whatsappTemplateId").toString();
            log.debug("Usando WhatsApp template: {} para: {}", templateId, phoneNumber);
        }

        log.debug("WhatsApp simulado enviado a: {}", phoneNumber);
    }

    @Override
    public boolean supports(Canal canal) {
        return Canal.WHATSAPP.equals(canal);
    }

    @Override
    public Canal getChannel() {
        return Canal.WHATSAPP;
    }

    @Override
    public void validate(Notification notification) {
        if (notification.getDestinatarios() == null || notification.getDestinatarios().isEmpty()) {
            throw new IllegalArgumentException(
                    "WhatsApp strategy requires at least one phone number");
        }

        if (notification.getMensaje() == null || notification.getMensaje().isBlank()) {
            throw new IllegalArgumentException("WhatsApp strategy requires a message body");
        }

        // Validar formato de números de teléfono (con código de país)
        for (String phone : notification.getDestinatarios()) {
            if (!phone.matches("^\\+?[1-9]\\d{1,14}$")) {
                throw new IllegalArgumentException(
                        "Invalid WhatsApp phone number format (must include country code): " + phone);
            }
        }
    }
}