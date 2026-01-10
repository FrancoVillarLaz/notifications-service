package franco.notifications.persistence.dto;

import franco.notifications.persistence.enums.Canal;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO para notificaciones personalizadas sin template
 */
@Setter
@Getter
public class CustomNotificationRequestDTO {
    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    @NotBlank(message = "El mensaje es obligatorio")
    private String mensaje;

    private Canal canal = Canal.EMAIL;

    @NotEmpty(message = "Debe haber al menos un destinatario")
    private List<@Email(message = "Email inválido") String> destinatarios;

    private Map<String, Object> metadata;
    private LocalDateTime programarPara;


}
