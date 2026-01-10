package proyect3.notifications.controller;

// El import que solicitaste
import franco.notifications.persistence.dto.*;

import proyect3.notifications.persistence.dto.*;
import proyect3.notifications.persistence.entities.Notification;
import proyect3.notifications.persistence.repositories.NotificationRepository;
import proyect3.notifications.service.NotificationFactory;
import proyect3.notifications.service.NotificationScheduledService;
import proyect3.notifications.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationScheduledService scheduledService;
    private final NotificationService notificationService;
    private final NotificationFactory factory;
    private final NotificationRepository notificationRepository;

    // --- Endpoints Modernos (con nombres corregidos)

    @PostMapping("/send")
    public ResponseEntity<ApiResponseDTO> enviarNotificacion(@Valid @RequestBody NotificationRequestDTO request) {
        log.info("Procesando notificación template={}, destinatarios={}",
                request.getTemplate(), request.getDestinatarios().size());

        Notification notification = factory.desdeTemplate(
                request.getTemplate(),
                request.getDestinatarios(),
                request.getVariables()
        );

        return procesarNotificacion(notification, request.getProgramarPara());
    }

    @PostMapping("/custom")
    public ResponseEntity<ApiResponseDTO> enviarPersonalizada(@Valid @RequestBody CustomNotificationRequestDTO request) {
        log.info("Creando notificación personalizada: {}", request.getTitulo());

        Notification notification = Notification.builder()
                .titulo(request.getTitulo())
                .mensaje(request.getMensaje())
                .canal(request.getCanal())
                .destinatarios(request.getDestinatarios())
                .metadata(request.getMetadata())
                .programarPara(request.getProgramarPara())
                .build();

        return procesarNotificacion(notification, request.getProgramarPara());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO> obtenerEstado(@PathVariable String id) {
        log.info("Consultando estado de notificación: {}", id);

        return notificationRepository.findById(id)
                .map(notification -> ApiResponseDTO.success(
                        "Notificación encontrada",
                        Map.of("notification", notification))
                )
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseDTO.error("Notificación no encontrada con id: " + id)));
    }

    @Deprecated
    @PostMapping("/bienvenida")
    public ResponseEntity<ApiResponseDTO> enviarBienvenida(
            @Valid @RequestBody RequestBienvenidaDTO request) {

        Notification notification = factory.bienvenida(
                request.getEmail(),
                request.getNombre(),
                request.getUsuario(),
                request.getPassword()
        );
        return procesarNotificacion(notification, null);
    }

    @Deprecated
    @PostMapping("/recordatorio-pago")
    public ResponseEntity<ApiResponseDTO> enviarRecordatorioPago(
            @Valid @RequestBody RequestRecordatorioPagoDTO request) {

        Notification notification = factory.recordatorioPago(
                request.getEmail(),
                request.getNombre(),
                request.getFechaPago(),
                request.getMonto(),
                request.getConcepto()
        );
        return procesarNotificacion(notification, null);
    }

    @Deprecated
    @PostMapping("/recuperar-password")
    public ResponseEntity<ApiResponseDTO> enviarRecuperarPassword(
            @Valid @RequestBody RequestRecuperarPasswordDTO request) {

        Notification notification = factory.recuperarPassword(
                request.getEmail(),
                request.getNombre(),
                request.getUrlRecuperacion()
        );
        return procesarNotificacion(notification, null);
    }

    // --- Métodos privados auxiliares (con nombres corregidos)

    private ResponseEntity<ApiResponseDTO> procesarNotificacion(Notification notification, LocalDateTime programarPara) {
        if (programarPara != null && programarPara.isAfter(LocalDateTime.now())) {
            scheduledService.guardarNotificacionProgramada(notification);

            log.info("Notificación programada: id={}, para={}", notification.getId(), programarPara);

            return ResponseEntity.ok(ApiResponseDTO.success(
                    "Notificación programada exitosamente",
                    Map.of(
                            "id", notification.getId(),
                            "programadaPara", programarPara.toString()
                    )
            ));
        } else {
            notificationService.enviarNotificacion(notification);

            log.info("Notificación enviada inmediatamente: id={}", notification.getId());

            return ResponseEntity.ok(ApiResponseDTO.success(
                    "Notificación enviada exitosamente",
                    Map.of("id", notification.getId())
            ));
        }
    }
}