package proyect3.notifications.service;

import proyect3.notifications.templates.EmailTemplate;
import proyect3.notifications.templates.TemplateProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailTemplateService {

    private final TemplateProvider provider;

    public EmailTemplate procesarTemplate(String nombre, Map<String, Object> variables) {
        return provider.getProcessed(nombre, "es_AR", variables == null ? Map.of() : variables);
    }
}
