package proyect3.notifications.notifier.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import proyect3.notifications.exception.ChannelNotSupportedException;
import proyect3.notifications.persistence.enums.Canal;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Gestor de estrategias de notificación (Factory + Strategy Pattern).
 * Selecciona automáticamente la estrategia correcta según el canal.
 */
@Slf4j
@Component
public class NotificationStrategyManager {

    private final Map<Canal, NotificationStrategy> strategyMap;

    /**
     * Spring inyecta automáticamente todas las implementaciones de NotificationStrategy.
     * Se construye un mapa para acceso O(1) por canal.
     */
    public NotificationStrategyManager(List<NotificationStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(
                        NotificationStrategy::getChannel,
                        strategy -> strategy
                ));

        log.info("NotificationStrategyManager inicializado con {} estrategias: {}",
                strategyMap.size(),
                strategyMap.keySet());
    }

    /**
     * Obtiene la estrategia adecuada para el canal especificado.
     *
     * @param canal El canal para el cual se necesita la estrategia
     * @return La estrategia que maneja ese canal
     * @throws ChannelNotSupportedException si no hay estrategia para el canal
     */
    public NotificationStrategy getStrategy(Canal canal) {
        NotificationStrategy strategy = strategyMap.get(canal);

        if (strategy == null) {
            log.error("No se encontró estrategia para el canal: {}", canal);
            throw new ChannelNotSupportedException(canal);
        }

        return strategy;
    }

    /**
     * Verifica si un canal está soportado.
     *
     * @param canal El canal a verificar
     * @return true si existe una estrategia para ese canal
     */
    public boolean isChannelSupported(Canal canal) {
        return strategyMap.containsKey(canal);
    }

    /**
     * Obtiene la lista de canales soportados.
     *
     * @return Lista de canales con estrategias implementadas
     */
    public List<Canal> getSupportedChannels() {
        return List.copyOf(strategyMap.keySet());
    }
}