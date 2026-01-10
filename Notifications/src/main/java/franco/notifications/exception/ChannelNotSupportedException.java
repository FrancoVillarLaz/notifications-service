package franco.notifications.exception;

import franco.notifications.persistence.enums.Canal;

public class ChannelNotSupportedException extends NotificationException {
    public ChannelNotSupportedException(Canal canal) {
        super("Canal no soportado: " + canal);
    }
}