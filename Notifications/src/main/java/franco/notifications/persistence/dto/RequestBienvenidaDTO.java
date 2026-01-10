package franco.notifications.persistence.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class RequestBienvenidaDTO extends BaseLegacyRequestDTO {
    // email y nombre se heredan de BaseLegacyRequestDTO

    @NotBlank(message = "El usuario es obligatorio")
    private String usuario;

    @NotBlank(message = "El password es obligatorio")
    private String password;
}
