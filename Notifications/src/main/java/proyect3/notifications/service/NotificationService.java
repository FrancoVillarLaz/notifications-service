package proyect3.notifications.service;

import proyect3.notifications.persistence.entities.Notification;

public interface NotificationService {
    void enviarNotificacion(Notification notification);
}
