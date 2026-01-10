package franco.notifications.persistence.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class RequestRecordatorioPagoDTO extends BaseLegacyRequestDTO {
    // email y nombre se heredan de BaseLegacyRequestDTO

    @NotBlank(message = "La fecha de pago es obligatoria")
    private String fechaPago;

    @NotBlank(message = "El monto es obligatorio")
    private String monto;

    @NotBlank(message = "El concepto es obligatorio")
    private String concepto;
}