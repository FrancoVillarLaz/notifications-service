package proyect3.notifications.persistence.mapper;

import proyect3.notifications.persistence.dto.NotificationDto;
import proyect3.notifications.persistence.entities.Notification;
import proyect3.notifications.persistence.enums.EstadoNotification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    /**
     * Convierte un objeto NotificationDto a Notification (entidad JPA)
     */
    public Notification toEntity(NotificationDto notificationDto) {
        return Notification.builder()
                .id(notificationDto.getId())
                .titulo(notificationDto.getTitulo())
                .mensaje(notificationDto.getMensaje())
                .destinatarios(notificationDto.getDestinatarios())
                .canal(notificationDto.getCanal())
                .metadata(notificationDto.getMetadata())
                .programarPara(notificationDto.getProgramarPara())
                .estado(determinarEstadoInicial(notificationDto))
                .build();
    }

    /**
     * Convierte una Notification (entidad JPA) a NotificationDto
     */
    public NotificationDto toDto(Notification entity) {
        return new NotificationDto(
                entity.getId(),
                entity.getTitulo(),
                entity.getMensaje(),
                entity.getDestinatarios(),
                entity.getCanal(),
                entity.getMetadata(),
                entity.getProgramarPara()
        );
    }

    /**
     * Determina el estado inicial de la notificación
     */
    private EstadoNotification determinarEstadoInicial(NotificationDto notificationDto) {
        if (notificationDto.getProgramarPara() != null) {
            return EstadoNotification.PROGRAMADO;
        }
        return EstadoNotification.PENDIENTE;
    }
}