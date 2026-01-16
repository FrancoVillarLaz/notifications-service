package proyect3.notifications.notifier.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import proyect3.notifications.exception.NotificationException;
import proyect3.notifications.persistence.entities.Notification;
import proyect3.notifications.persistence.enums.Canal;

/**
 * Estrategia para enviar Push Notifications.
 * Implementación de ejemplo - requiere integración con FCM, APNS, etc.
 */
@Slf4j
@Component
public class PushNotificationStrategy implements NotificationStrategy {

    @Override
    public void send(Notification notification) {
        validate(notification);

        try {
            // TODO: Integrar con Firebase Cloud Messaging (FCM) o Apple Push Notification Service (APNS)
            log.info("Enviando Push Notification a {} dispositivos - Título: {}",
                    notification.getDestinatarios().size(),
                    notification.getTitulo());

            for (String deviceToken : notification.getDestinatarios()) {
                sendPushToDevice(deviceToken, notification);
            }

            log.info("Push Notification enviada exitosamente a {} dispositivos",
                    notification.getDestinatarios().size());

        } catch (Exception e) {
            log.error("Error al enviar Push Notification", e);
            throw new NotificationException("Error al enviar Push Notification", e);
        }
    }

    private void sendPushToDevice(String deviceToken, Notification notification) {
        // TODO: Implementar integración real con FCM
        // Ejemplo con Firebase Admin SDK:
        // Message message = Message.builder()
        //     .setToken(deviceToken)
        //     .setNotification(Notification.builder()
        //         .setTitle(notification.getTitulo())
        //         .setBody(notification.getMensaje())
        //         .build())
        //     .putAllData(notification.getMetadata())
        //     .build();
        //
        // FirebaseMessaging.getInstance().send(message);

        log.debug("Push simulado enviado al dispositivo: {}",
                deviceToken.substring(0, Math.min(10, deviceToken.length())) + "...");
    }

    @Override
    public boolean supports(Canal canal) {
        return Canal.PUSH_NOTIFICATION.equals(canal);
    }

    @Override
    public Canal getChannel() {
        return Canal.PUSH_NOTIFICATION;
    }

    @Override
    public void validate(Notification notification) {
        if (notification.getDestinatarios() == null || notification.getDestinatarios().isEmpty()) {
            throw new IllegalArgumentException(
                    "Push strategy requires at least one device token");
        }

        if (notification.getTitulo() == null || notification.getTitulo().isBlank()) {
            throw new IllegalArgumentException("Push strategy requires a title");
        }

        if (notification.getMensaje() == null || notification.getMensaje().isBlank()) {
            throw new IllegalArgumentException("Push strategy requires a message body");
        }

        // Validar longitud del título y mensaje para push notifications
        if (notification.getTitulo().length() > 65) {
            log.warn("Push title exceeds 65 characters, may be truncated on some devices");
        }

        if (notification.getMensaje().length() > 240) {
            log.warn("Push message exceeds 240 characters, may be truncated on some devices");
        }
    }
}