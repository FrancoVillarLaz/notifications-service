package proyect3.notifications.persistence.repositories;

import proyect3.notifications.persistence.entities.EmailTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmailTemplateRepository extends JpaRepository<EmailTemplateEntity, Long> {

    @Query("""
        select t from EmailTemplateEntity t
        where t.templateKey = :key
          and t.active = true
          and (t.locale = :locale or t.locale is null)
        order by case when t.locale = :locale then 0 else 1 end, t.version desc
        """)
    Optional<EmailTemplateEntity> findBestMatch(@Param("key") String key, @Param("locale") String locale);
}
