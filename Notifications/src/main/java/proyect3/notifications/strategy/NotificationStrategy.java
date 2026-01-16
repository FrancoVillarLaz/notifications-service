package proyect3.notifications.strategy;

import proyect3.notifications.persistence.entities.Notification;
import proyect3.notifications.persistence.enums.Canal;

/**
 * Patrón Strategy para el envío de notificaciones multicanal.
 * Cada implementación maneja un canal específico: EMAIL, SMS, PUSH, WHATSAPP.
 */
public interface NotificationStrategy {

    /**
     * Envía una notificación a través del canal implementado.
     * @param notification La notificación a enviar con su contenido y destinatarios
     */
    void send(Notification notification);

    /**
     * Indica si esta estrategia soporta el canal especificado.
     * @param canal El canal a verificar
     * @return true si esta estrategia puede manejar el canal
     */
    boolean supports(Canal canal);

    /**
     * Retorna el canal que maneja esta estrategia.
     * @return El canal soportado
     */
    Canal getChannel();

    /**
     * Valida que la notificación tenga toda la información requerida para este canal.
     * @param notification La notificación a validar
     * @throws IllegalArgumentException si falta información requerida
     */
    void validate(Notification notification);
}
