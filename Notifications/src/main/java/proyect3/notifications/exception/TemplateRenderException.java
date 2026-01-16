package proyect3.notifications.exception;

/**
 * Excepción lanzada cuando ocurre un error al renderizar un template.
 */
public class TemplateRenderException extends RuntimeException {
    public TemplateRenderException(String message) {
        super(message);
    }

    public TemplateRenderException(String message, Throwable cause) {
        super(message, cause);
    }
}