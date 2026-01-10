package proyect3.notifications.persistence.dto;


import proyect3.notifications.persistence.enums.Canal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class NotificationDto {
    private String id;
    private String titulo;
    private String mensaje;
    private List<String> destinatarios;
    private Canal canal;
    private Map<String, Object> metadata;
    private LocalDateTime programarPara;
}
