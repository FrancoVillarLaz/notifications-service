package franco.notifications.notifier;

import franco.notifications.persistence.entities.Notification;
import franco.notifications.persistence.enums.Canal;

public interface Notifier {
    void send(Notification notification);
    boolean soporta(Canal canal);

}