package franco.notifications.persistence.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public abstract class BaseLegacyRequestDTO {

    @Email(message = "Email inválido")
    @NotBlank(message = "El email es obligatorio")
    private String email;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
}
