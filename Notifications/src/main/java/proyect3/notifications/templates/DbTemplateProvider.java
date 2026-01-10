package proyect3.notifications.templates;

import proyect3.notifications.exception.TemplateNotFoundException;
import proyect3.notifications.persistence.entities.EmailTemplateEntity;
import proyect3.notifications.persistence.repositories.EmailTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DbTemplateProvider implements TemplateProvider {

    private final EmailTemplateRepository repo;
    private final TemplateEngine engine;

    @Cacheable(value = "templates.raw", key = "#key + '|' + (#locale == null ? '' : #locale)")
    @Transactional(readOnly = true)
    public EmailTemplate loadRaw(String key, String locale) {
        EmailTemplateEntity e = repo.findBestMatch(key, locale)
                .orElseThrow(() -> new TemplateNotFoundException(key));
        return EmailTemplate.builder()
                .nombre(e.getTemplateKey())
                .asunto(e.getSubject())
                .cuerpoTexto(e.getTextBody())
                .cuerpoHtml(e.getHtmlBody())
                .variablesDefecto(e.getDefaultVars())
                .build();
    }

    @Override
    public EmailTemplate getProcessed(String key, String locale, Map<String, Object> variables) {
        EmailTemplate raw = loadRaw(key, locale);
        Map<String, Object> vars = new HashMap<>();
        if (raw.getVariablesDefecto() != null) vars.putAll(raw.getVariablesDefecto());
        if (variables != null) vars.putAll(variables);
        return engine.procesarEmailTemplate(raw, vars);
    }
}
