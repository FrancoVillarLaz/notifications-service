package proyect3.notifications.persistence.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO para requests de notificaciones usando templates
 */
@Getter
public class NotificationRequestDTO {
    @NotBlank(message = "El template es obligatorio")
    private String template;

    @NotEmpty(message = "Debe haber al menos un destinatario")
    private List<@Email(message = "Email inválido") String> destinatarios;

    private Map<String, Object> variables;
    private LocalDateTime programarPara;

    public Map<String, Object> getVariables() { return variables != null ? variables : Map.of(); }

}
