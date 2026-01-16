package proyect3.notifications.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import proyect3.notifications.persistence.entities.NotificationTemplate;
import proyect3.notifications.persistence.enums.Canal;

import java.util.Optional;
import java.util.List;

@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, String> {

    /**
     * Busca un template activo por su código.
     */
    Optional<NotificationTemplate> findByCodeAndActiveTrue(String code);

    /**
     * Busca templates que soporten un canal específico.
     */
    @Query("SELECT t FROM NotificationTemplate t JOIN t.supportedChannels c WHERE c = :canal AND t.active = true")
    List<NotificationTemplate> findByChannelAndActiveTrue(@Param("canal") Canal canal);

    /**
     * Busca todos los templates activos.
     */
    List<NotificationTemplate> findByActiveTrue();

    /**
     * Verifica si existe un template por código.
     */
    boolean existsByCode(String code);
}
