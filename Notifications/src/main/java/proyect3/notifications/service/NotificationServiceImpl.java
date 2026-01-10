package proyect3.notifications.service;


import proyect3.notifications.exception.ChannelNotSupportedException;
import proyect3.notifications.exception.NotificationException;
import proyect3.notifications.notifier.Notifier;
import proyect3.notifications.persistence.entities.Notification;
import proyect3.notifications.persistence.repositories.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
// Clase que implementa el servicio de notificaciones
// Utiliza la inyección de dependencias para obtener una lista de notifiers
// y envía las notificaciones a través del notifier adecuado según el canal.
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final List<Notifier> notifiers;
    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(List<Notifier> notifiers,
                                   NotificationRepository notificationRepository) {
        this.notifiers = notifiers;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void enviarNotificacion(Notification notification) {
        log.info("Procesando notificación ID: {} por canal: {} a: {}",
                notification.getId(), notification.getCanal(), notification.getDestinatarios());

        Notification entity = notificationRepository.save(notification);

        try {
            Notifier notifierSeleccionado = null;
            for (Notifier notifier : notifiers) {
                if (notifier.soporta(notification.getCanal())) {
                    notifierSeleccionado = notifier;
                    break;
                }
            }

            if (notifierSeleccionado == null) {
                String error = "No se encontró un notifier para el canal: " + notification.getCanal();
                log.error(error);
                entity.marcarComoFallido(error);
                notificationRepository.save(entity);
                throw new ChannelNotSupportedException(notification.getCanal());
            }

            notifierSeleccionado.send(entity);

            entity.marcarComoEnviado();
            notificationRepository.save(entity);

            log.info("Notificación ID: {} enviada exitosamente por: {}",
                    notification.getId(), notifierSeleccionado.getClass().getSimpleName());

        } catch (NotificationException e) {
            log.error("Error al enviar notificación ID: {} - {}", notification.getId(), e.getMessage());
            entity.marcarComoFallido(e.getMessage());
            notificationRepository.save(entity);
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al enviar notificación ID: {}", notification.getId(), e);
            entity.marcarComoFallido("Error inesperado: " + e.getMessage());
            notificationRepository.save(entity);
            throw new NotificationException("Error inesperado al enviar notificación", e);
        }
    }

    /**
     * Obtiene el historial de notificaciones
     */
    @Transactional(readOnly = true)
    public List<Notification> obtenerHistorial() {
        return notificationRepository.findAll();
    }

    /**
     * Obtiene una notificación por ID
     */
    @Transactional(readOnly = true)
    public Notification obtenerPorId(String id) {
        return notificationRepository.findById(id).orElse(null);
    }

    /**
     * Reintenta enviar notificaciones fallidas
     */
    public void reintentarNotificacionesFallidas(int maxIntentos) {
        List<Notification> fallidas = notificationRepository
                .findNotificacionesParaReintento(maxIntentos);

        log.info("Reintentando {} notificaciones fallidas", fallidas.size());

        for (Notification entity : fallidas) {
            try {
                enviarNotificacion(entity);
            } catch (Exception e) {
                log.warn("Falló reintento para notificación ID: {}", entity.getId());
            }
        }
    }
}