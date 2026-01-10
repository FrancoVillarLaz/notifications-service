package franco.notifications.persistence.repositories;


import franco.notifications.persistence.entities.Notification;
import franco.notifications.persistence.enums.Canal;
import franco.notifications.persistence.enums.EstadoNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {

    List<Notification> findByEstado(EstadoNotification estado);

    List<Notification> findByCanal(Canal canal);

    @Query("SELECT n FROM Notification n JOIN n.destinatarios d WHERE d = :destinatario")
    List<Notification> findByDestinatario(@Param("destinatario") String destinatario);

    List<Notification> findByFechaCreacionBetween(LocalDateTime inicio, LocalDateTime fin);

    @Query("SELECT n FROM Notification n WHERE n.estado = 'PROGRAMADO' AND n.programarPara <= :fecha")
    List<Notification> findNotificacionesProgramadasParaEnviar(@Param("fecha") LocalDateTime fecha);

    @Query("SELECT n FROM Notification n WHERE n.estado = 'FALLIDO' AND n.intentos < :maxIntentos")
    List<Notification> findNotificacionesParaReintento(@Param("maxIntentos") Integer maxIntentos);

    @Query("SELECT n.canal, COUNT(n) FROM Notification n GROUP BY n.canal")
    List<Object[]> findEstadisticasPorCanal();

    @Query("SELECT n.estado, COUNT(n) FROM Notification n GROUP BY n.estado")
    List<Object[]> findEstadisticasPorEstado();

    Page<Notification> findByEstadoAndCanal(EstadoNotification estado, Canal canal, Pageable pageable);

    @Query(value = "SELECT * FROM notifications n WHERE JSON_EXTRACT(n.metadata, '$.template') = :template", nativeQuery = true)
    List<Notification> findByTemplate(@Param("template") String template);


}