package proyect3.notifications.exception;

import proyect3.notifications.persistence.enums.Canal;

public class ChannelNotSupportedException extends NotificationException {
    public ChannelNotSupportedException(Canal canal) {
        super("Canal no soportado: " + canal);
    }
}