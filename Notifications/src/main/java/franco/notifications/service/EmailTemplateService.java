package franco.notifications.service;

import franco.notifications.templates.EmailTemplate;
import franco.notifications.templates.TemplateProvider;
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
