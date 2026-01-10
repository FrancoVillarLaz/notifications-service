package proyect3.notifications.persistence.enums;

import lombok.Getter;

@Getter
/**
 * Enum que representa los diferentes estados de una notificación.
 * Cada estado tiene una descripción asociada.
 */
public enum EstadoNotification {
    PENDIENTE("Pendiente de envío"),
    ENVIADO("Enviado exitosamente"),
    FALLIDO("Falló el envío"),
    PROGRAMADO("Programado para envío futuro"),
    CANCELADO("Cancelado"),
    REINTENTANDO("Reintentando envío");

    private final String descripcion;

    EstadoNotification(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}