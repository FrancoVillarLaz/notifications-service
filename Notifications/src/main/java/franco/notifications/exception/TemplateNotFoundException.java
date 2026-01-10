package franco.notifications.exception;

public class TemplateNotFoundException extends NotificationException {
    public TemplateNotFoundException(String templateName) {
        super("Template no encontrado: " + templateName);
    }
}