package proyect3.notifications.service;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proyect3.notifications.exception.TemplateNotFoundException;
import proyect3.notifications.exception.TemplateRenderException;
import proyect3.notifications.persistence.entities.NotificationTemplate;
import proyect3.notifications.persistence.repositories.NotificationTemplateRepository;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Servicio para renderizar templates con Mustache.
 * Combina layout + contenido + variables para generar el mensaje final.
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class TemplateRenderingService {

    private final NotificationTemplateRepository templateRepository;
    private final MustacheFactory mustacheFactory;

    public TemplateRenderingService(NotificationTemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
        this.mustacheFactory = new DefaultMustacheFactory();
    }

    /**
     * Renderiza un template completo: layout + contenido + variables.
     *
     * @param templateCode Código del template (ej: "BIENVENIDA")
     * @param variables Variables a inyectar en el template
     * @return Map con "subject", "htmlBody", "textBody"
     */
    public Map<String, String> renderTemplate(String templateCode, Map<String, Object> variables) {
        log.debug("Renderizando template: {} con {} variables", templateCode, variables.size());

        // 1. Buscar el template en BD
        NotificationTemplate template = templateRepository
                .findByCodeAndActiveTrue(templateCode)
                .orElseThrow(() -> new TemplateNotFoundException(templateCode));

        // 2. Validar variables requeridas
        if (!template.hasAllRequiredVariables(variables)) {
            throw new TemplateRenderException(
                    "Faltan variables requeridas. Esperadas: " + template.getRequiredVariables());
        }

        // 3. Combinar variables por defecto con las proporcionadas
        Map<String, Object> mergedVariables = new HashMap<>();
        if (template.getDefaultVariables() != null) {
            mergedVariables.putAll(template.getDefaultVariables());
        }
        mergedVariables.putAll(variables);

        // 4. Renderizar cada parte
        String subject = renderMustache(template.getSubjectTemplate(), mergedVariables);
        String textBody = renderMustache(template.getBodyTextTemplate(), mergedVariables);

        // 5. Renderizar HTML: primero el contenido, luego insertarlo en el layout
        String htmlContent = renderMustache(template.getBodyHtmlTemplate(), mergedVariables);
        String htmlBody = htmlContent;

        if (template.getLayout() != null) {
            // Insertar el contenido renderizado en el layout
            htmlBody = template.getLayout().render(htmlContent);
        }

        log.info("Template '{}' renderizado exitosamente", templateCode);

        return Map.of(
                "subject", subject,
                "htmlBody", htmlBody,
                "textBody", textBody
        );
    }

    /**
     * Renderiza un string usando Mustache.
     */
    private String renderMustache(String template, Map<String, Object> variables) {
        if (template == null || template.isBlank()) {
            return "";
        }

        try {
            Mustache mustache = mustacheFactory.compile(new StringReader(template), "template");
            StringWriter writer = new StringWriter();
            mustache.execute(writer, variables);
            return writer.toString();
        } catch (Exception e) {
            log.error("Error renderizando template Mustache", e);
            throw new TemplateRenderException("Error al renderizar template", e);
        }
    }

    /**
     * Obtiene un template por código para inspección.
     */
    public NotificationTemplate getTemplate(String templateCode) {
        return templateRepository
                .findByCodeAndActiveTrue(templateCode)
                .orElseThrow(() -> new TemplateNotFoundException(templateCode));
    }
}