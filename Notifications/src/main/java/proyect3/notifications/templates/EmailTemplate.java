package proyect3.notifications.templates;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailTemplate {
    private String nombre;
    private String asunto;
    private String cuerpoTexto;
    private String cuerpoHtml;
    private Map<String, Object> variablesDefecto;
}
