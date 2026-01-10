package franco.notifications.persistence.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class RequestRecuperarPasswordDTO extends BaseLegacyRequestDTO {
    // email y nombre se heredan de BaseLegacyRequestDTO

    @NotBlank(message = "La URL de recuperación es obligatoria")
    private String urlRecuperacion;
}
