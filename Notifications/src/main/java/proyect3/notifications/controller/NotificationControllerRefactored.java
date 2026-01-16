package proyect3.notifications.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyect3.notifications.exception.NotificationException;
import proyect3.notifications.persistence.entities.Notification;
import proyect3.notifications.persistence.enums.Canal;
import proyect3.notifications.service.NotificationScheduledService;
import proyect3.notifications.service.NotificationServiceRefactored;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Controlador refactorizado con soporte multicanal vía Strategy Pattern.
 * El endpoint /template ahora puede enviar por cualquier canal soportado.
 */
@Slf4j
@RestController
@RequestMapping("/api/notifications")
public class NotificationControllerRefactored {

    private final NotificationScheduledService notificationScheduledService;
    private final NotificationServiceRefactored notificationService;

    public NotificationControllerRefactored(
            NotificationScheduledService notificationScheduledService,
            NotificationServiceRefactored notificationService) {
        this.notificationScheduledService = notificationScheduledService;
        this.notificationService = notificationService;
    }

    /**
     * Endpoint principal: envía notificaciones usando templates de BD.
     * Soporta todos los canales vía Strategy Pattern.
     *
     * Ejemplo EMAIL:
     * {
     *   "template": "BIENVENIDA",
     *   "canal": "EMAIL",
     *   "destinatarios": ["user@example.com"],
     *   "variables": {
     *     "nombre": "Juan",
     *     "usuario": "juan123",
     *     "password": "temp123"
     *   }
     * }
     *
     * Ejemplo SMS:
     * {
     *   "template": "CODIGO_VERIFICACION",
     *   "canal": "SMS",
     *   "destinatarios": ["+5491155551234"],
     *   "variables": {
     *     "codigo": "123456"
     *   }
     * }
     *
     * Ejemplo WhatsApp:
     * {
     *   "template": "RECORDATORIO_PAGO",
     *   "canal": "WHATSAPP",
     *   "destinatarios": ["+5491155551234"],
     *   "variables": {
     *     "nombre": "Juan",
     *     "monto": "1500",
     *     "fecha": "2024-01-20"
     *   }
     * }
     *
     * Ejemplo Push:
     * {
     *   "template": "NUEVA_MENSAJE",
     *   "canal": "PUSH_NOTIFICATION",
     *   "destinatarios": ["device_token_abc123"],
     *   "variables": {
     *     "remitente": "María",
     *     "preview": "Hola, cómo estás?"
     *   }
     * }
     */
    @PostMapping("/template")
    public ResponseEntity<Map<String, String>> enviarNotificacionPorTemplate(
            @RequestBody Map<String, Object> payload
    ) {
        try {
            String template = (String) payload.get("template");
            String canalStr = (String) payload.getOrDefault("canal", "EMAIL");
            List<String> destinatarios = (List<String>) payload.get("destinatarios");
            Map<String, Object> variables = (Map<String, Object>)
                    payload.getOrDefault("variables", Map.of());

            Canal canal = Canal.valueOf(canalStr.toUpperCase());

            Notification notification = notificationService.enviarConTemplate(
                    template,
                    destinatarios,
                    variables,
                    canal);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Notificación enviada exitosamente",
                    "id", notification.getId(),
                    "canal", canal.toString()
            ));
        } catch (IllegalArgumentException e) {
            log.error("Canal no válido", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "status", "error",
                            "message", "Canal no válido: " + e.getMessage()
                    ));
        } catch (Exception e) {
            log.error("Error al enviar notificación por template", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "status", "error",
                            "message", e.getMessage()
                    ));
        }
    }

    /**
     * Endpoint para notificaciones personalizadas sin template.
     */
    @PostMapping("/personalizada")
    public ResponseEntity<Map<String, String>> crearNotificacionPersonalizada(
            @RequestParam String titulo,
            @RequestParam String mensaje,
            @RequestParam String canal,
            @RequestParam List<String> destinatarios,
            @RequestParam(required = false) Map<String, Object> metadata,
            @RequestParam(required = false) String programarPara
    ) {
        try {
            var builder = Notification.builder()
                    .titulo(titulo)
                    .mensaje(mensaje)
                    .canal(Canal.valueOf(canal.toUpperCase()))
                    .destinatarios(destinatarios);

            if (metadata != null) {
                builder.metadata(metadata);
            }
            if (programarPara != null && !programarPara.isBlank()) {
                builder.programarPara(LocalDateTime.parse(programarPara));
            }

            var notification = builder.build();

            if (programarPara != null && !programarPara.isBlank()) {
                notificationScheduledService.guardarNotificacionProgramada(notification);
                return ResponseEntity.ok(Map.of(
                        "status", "success",
                        "message", "Notificación programada exitosamente",
                        "id", notification.getId()
                ));
            } else {
                notification = notificationService.enviarNotificacion(notification);
                return ResponseEntity.ok(Map.of(
                        "status", "success",
                        "message", "Notificación enviada exitosamente",
                        "id", notification.getId()
                ));
            }
        } catch (Exception e) {
            log.error("Error al crear notificación personalizada", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "status", "error",
                            "message", e.getMessage()
                    ));
        }
    }

    /**
     * Endpoint para consultar canales soportados.
     */
    @GetMapping("/canales-soportados")
    public ResponseEntity<Map<String, Object>> canalesSoportados() {
        // Esta información vendría del StrategyManager
        return ResponseEntity.ok(Map.of(
                "canales", List.of("EMAIL", "SMS", "WHATSAPP", "PUSH_NOTIFICATION"),
                "total", 4
        ));
    }

    @ExceptionHandler(NotificationException.class)
    public ResponseEntity<Map<String, String>> handleNotificationException(
            NotificationException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "status", "error",
                        "message", e.getMessage()
                ));
    }
}