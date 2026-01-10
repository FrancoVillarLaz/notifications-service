package franco.notifications.service;

import franco.notifications.persistence.entities.Notification;

public interface NotificationService {
    void enviarNotificacion(Notification notification);
}
