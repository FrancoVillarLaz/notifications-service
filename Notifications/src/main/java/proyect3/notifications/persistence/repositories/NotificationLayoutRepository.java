package proyect3.notifications.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import proyect3.notifications.persistence.entities.NotificationLayout;

import java.util.Optional;
import java.util.List;

@Repository
public interface NotificationLayoutRepository extends JpaRepository<NotificationLayout, String> {

    Optional<NotificationLayout> findByName(String name);

    List<NotificationLayout> findByActiveTrue();
}

