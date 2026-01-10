package franco.notifications.templates;

import java.util.Map;

public interface TemplateProvider {

    EmailTemplate getProcessed(String key, String locale, Map<String, Object> variables);
}
