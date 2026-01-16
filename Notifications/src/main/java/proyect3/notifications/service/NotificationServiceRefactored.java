package proyect3.notifications.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proyect3.notifications.exception.NotificationException;
import proyect3.notifications.persistence.entities.Notification;
import proyect3.notifications.persistence.enums.Canal;
import proyect3.notifications.persistence.repositories.NotificationRepository;
import proyect3.notifications.notifier.strategy.NotificationStrategy;
import proyect3.notifications.notifier.strategy.NotificationStrategyManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio principal de notificaciones refactorizado.
 * Usa Strategy Pattern para multicanal y templates desde BD.
 */
@Slf4j
@Service
@Transactional
public class NotificationServiceRefactored {

    private final NotificationRepository notificationRepository;
    private final NotificationStrategyManager strategyManager;
    private final TemplateRenderingService templateRenderingService;

    public NotificationServiceRefactored(
            NotificationRepository notificationRepository,
            NotificationStrategyManager strategyManager,
            TemplateRenderingService templateRenderingService) {
        this.notificationRepository = notificationRepository;
        this.strategyManager = strategyManager;
        this.templateRenderingService = templateRenderingService;
    }

    /**
     * Envía una notificación usando un template desde la BD.
     *
     * @param templateCode Código del template (ej: "BIENVENIDA")
     * @param destinatarios Lista de destinatarios
     * @param variables Variables para el template
     * @param canal Canal de envío
     * @return La notificación creada y enviada
     */
    public Notification enviarConTemplate(
            String templateCode,
            List<String> destinatarios,
            Map<String, Object> variables,
            Canal canal) {

        log.info("Enviando notificación con template '{}' a {} destinatarios por {}",
                templateCode, destinatarios.size(), canal);

        try {
            // 1. Renderizar el template desde BD
            Map<String, String> rendered = templateRenderingService.renderTemplate(
                    templateCode, variables);

            // 2. Crear la notificación
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("template", templateCode);
            metadata.put("cuerpoTexto", rendered.get("textBody"));

            Notification notification = Notification.builder()
                    .titulo(rendered.get("subject"))
                    .mensaje(rendered.get("htmlBody"))
                    .canal(canal)
                    .destinatarios(destinatarios)
                    .metadata(metadata)
                    .build();

            // 3. Enviar usando la estrategia apropiada
            return enviarNotificacion(notification);

        } catch (Exception e) {
            log.error("Error enviando notificación con template '{}'", templateCode, e);
            throw new NotificationException(
                    "Error al enviar notificación con template: " + templateCode, e);
        }
    }

    /**
     * Envía una notificación usando el patrón Strategy.
     * Selecciona automáticamente la estrategia según el canal.
     */
    public Notification enviarNotificacion(Notification notification) {
        log.info("Procesando notificación ID: {} por canal: {} a: {}",
                notification.getId(),
                notification.getCanal(),
                notification.getDestinatarios());

        // 1. Persistir la notificación
        Notification entity = notificationRepository.save(notification);

        try {
            // 2. Obtener la estrategia para el canal
            NotificationStrategy strategy = strategyManager.getStrategy(notification.getCanal());

            // 3. Validar antes de enviar
            strategy.validate(entity);

            // 4. Enviar usando la estrategia
            strategy.send(entity);

            // 5. Marcar como enviada
            entity.marcarComoEnviado();
            notificationRepository.save(entity);

            log.info("Notificación ID: {} enviada exitosamente por: {}",
                    notification.getId(),
                    strategy.getClass().getSimpleName());

            return entity;

        } catch (Exception e) {
            log.error("Error al enviar notificación ID: {} - {}",
                    notification.getId(), e.getMessage());
            entity.marcarComoFallido(e.getMessage());
            notificationRepository.save(entity);
            throw new NotificationException(
                    "Error al enviar notificación por " + notification.getCanal(), e);
        }
    }

    /**
     * Obtiene el historial de notificaciones.
     */
    @Transactional(readOnly = true)
    public List<Notification> obtenerHistorial() {
        return notificationRepository.findAll();
    }

    /**
     * Obtiene una notificación por ID.
     */
    @Transactional(readOnly = true)
    public Notification obtenerPorId(String id) {
        return notificationRepository.findById(id).orElse(null);
    }

    /**
     * Reintenta enviar notificaciones fallidas.
     */
    public void reintentarNotificacionesFallidas(int maxIntentos) {
        List<Notification> fallidas = notificationRepository
                .findNotificacionesParaReintento(maxIntentos);

        log.info("Reintentando {} notificaciones fallidas", fallidas.size());

        for (Notification entity : fallidas) {
            try {
                enviarNotificacion(entity);
            } catch (Exception e) {
                log.warn("Falló reintento para notificación ID: {}", entity.getId());
            }
        }
    }
}