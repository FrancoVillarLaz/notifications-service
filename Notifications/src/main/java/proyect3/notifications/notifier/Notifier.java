package proyect3.notifications.notifier;

import proyect3.notifications.persistence.entities.Notification;
import proyect3.notifications.persistence.enums.Canal;

public interface Notifier {
    void send(Notification notification);
    boolean soporta(Canal canal);

}