package franco.notifications.templates;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TemplateEngine {

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{([^}]+)\\}\\}");
    /**
     * Procesa un template reemplazando variables del formato {{variable}}
     */
    public String procesarTemplate(String template, Map<String, Object> variables) {
        if (template == null) {
            return "";
        }

        Matcher matcher = VARIABLE_PATTERN.matcher(template);
        StringBuffer resultado = new StringBuffer();

        while (matcher.find()) {
            String nombreVariable = matcher.group(1).trim();
            Object valor = variables.get(nombreVariable);
            String reemplazo = valor != null ? valor.toString() : "";
            matcher.appendReplacement(resultado, Matcher.quoteReplacement(reemplazo));
        }

        matcher.appendTail(resultado);
        return resultado.toString();
    }

    /**
     * Procesa un EmailTemplate completo con las variables proporcionadas
     */
    public EmailTemplate procesarEmailTemplate(EmailTemplate template, Map<String, Object> variables) {
        Map<String, Object> variablesCombinadas = new HashMap<>();
        if (template.getVariablesDefecto() != null) {
            variablesCombinadas.putAll(template.getVariablesDefecto());
        }
        variablesCombinadas.putAll(variables);

        return EmailTemplate.builder()
                .nombre(template.getNombre())
                .asunto(procesarTemplate(template.getAsunto(), variablesCombinadas))
                .cuerpoTexto(procesarTemplate(template.getCuerpoTexto(), variablesCombinadas))
                .cuerpoHtml(procesarTemplate(template.getCuerpoHtml(), variablesCombinadas))
                .variablesDefecto(template.getVariablesDefecto())
                .build();
    }
}