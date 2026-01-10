package proyect3.notifications.service;

import proyect3.notifications.persistence.entities.Notification;
import proyect3.notifications.persistence.enums.Canal;
import proyect3.notifications.templates.EmailTemplate;
import proyect3.notifications.templates.TemplateProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class NotificationFactory {

    private final TemplateProvider templateProvider;

    public Notification desdeTemplate(String templateKey, List<String> destinatarios, Map<String, Object> variables) {
        EmailTemplate template = templateProvider.getProcessed(templateKey, "es_AR", variables == null ? Map.of() : variables);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("template", templateKey);
        metadata.put("cuerpoTexto", template.getCuerpoTexto());

        return Notification.builder()
                .titulo(template.getAsunto())
                .mensaje(template.getCuerpoHtml())
                .canal(Canal.EMAIL)
                .destinatarios(destinatarios)
                .metadata(metadata)
                .build();
    }

    // legacy convenience methods (siguen funcionando)
    public Notification bienvenida(String email, String nombre, String usuario, String password) {
        return desdeTemplate("BIENVENIDA", Collections.singletonList(email),
                Map.of("nombre", nombre, "usuario", usuario, "password", password));
    }

    public Notification recordatorioPago(String email, String nombre, String fechaPago, String monto, String concepto) {
        return desdeTemplate("RECORDATORIO_PAGO", Collections.singletonList(email),
                Map.of("nombre", nombre, "fechaPago", fechaPago, "monto", monto, "concepto", concepto));
    }

    public Notification recuperarPassword(String email, String nombre, String urlRecuperacion) {
        return desdeTemplate("RECUPERAR_PASSWORD", Collections.singletonList(email),
                Map.of("nombre", nombre, "urlRecuperacion", urlRecuperacion));
    }
}
