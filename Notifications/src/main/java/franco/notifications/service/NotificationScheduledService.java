package franco.notifications.service;

import franco.notifications.persistence.entities.Notification;
import franco.notifications.persistence.enums.EstadoNotification;
import franco.notifications.persistence.repositories.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional
public class NotificationScheduledService{
    private final NotificationRepository notificationRepository;
    private final NotificationServiceImpl notificationServiceImpl;

    public NotificationScheduledService(NotificationRepository notificationRepository, NotificationServiceImpl notificationServiceImpl) {
        this.notificationRepository = notificationRepository;
        this.notificationServiceImpl = notificationServiceImpl;
    }

    public ResponseEntity<Map<String, String>> guardarNotificacionProgramada(Notification notification) {
        try {
            notification.setEstado(EstadoNotification.PROGRAMADO);
            notificationRepository.save(notification);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Notificación programada guardada exitosamente"
            ));
        } catch (Exception e) {
            log.error("Error al guardar notificación programada ID: {}", notification.getId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "error",
                            "message", e.getMessage()
                    ));
        }
    }

    /**
     * Método programado que se ejecuta cada minuto para enviar notificaciones programadas.
     * Busca notificaciones con estado PROGRAMADO y las envía.
     */

@Scheduled(fixedRateString = "${notifications.scheduler.fixed-rate:60000}")
@Transactional
public void enviarNotificacionesProgramadas() {
    LocalDateTime ahora = LocalDateTime.now();
    List<Notification> programadas = notificationRepository.findNotificacionesProgramadasParaEnviar(ahora);

    for (Notification notification : programadas) {
        if (notification.getProgramarPara() != null && !ahora.isBefore(notification.getProgramarPara())) {
            try {
                notificationServiceImpl.enviarNotificacion(notification);
                notification.setEstado(EstadoNotification.ENVIADO);
                notificationRepository.save(notification);
            } catch (Exception e) {
                log.error("Error al enviar notificación programada ID: {}", notification.getId(), e);
                notification.setEstado(EstadoNotification.FALLIDO);
                notificationRepository.save(notification);
            }
        }
        else {
            log.info("Notificación ID: {} no está lista para enviar, programada para: {}", notification.getId(), notification.getProgramarPara());

        }

}
    log.info("Envío de notificaciones programadas completado. Total enviadas: {}", programadas.size());
}
}
