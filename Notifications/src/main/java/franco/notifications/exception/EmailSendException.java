package franco.notifications.exception;

public class EmailSendException extends NotificationException {
    public EmailSendException(String message, Throwable cause) {
        super("Error al enviar email: " + message, cause);
    }
}