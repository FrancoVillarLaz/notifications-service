package franco.notifications.persistence.entities;

import franco.notifications.persistence.enums.Canal;
import franco.notifications.persistence.util.JsonMapConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "email_templates",
        uniqueConstraints = @UniqueConstraint(columnNames = {"template_key","version","locale"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EmailTemplateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //identificador lógico de la plantilla, por ejemplo "BIENVENIDA", "RECUPERAR_PASSWORD".
    @Column(name = "template_key", nullable = false, length = 120)
    private String templateKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 30)
    private Canal channel;

    //Permite tener el mismo template en varios idomas
    @Column(name = "locale", length = 10)
    private String locale;

    //Permite historizar cambios. Cuando creás una nueva versión, incrementás version y podés mantener la anterior como historial (active=false o conservando active true si querés varias activas).
    @Column(name = "version", nullable = false)
    private Integer version;

    //Asunto del Email
    @Column(name = "subject", length = 300)
    private String subject;

    @Lob
    @Column(name = "text_body")
    private String textBody;

    @Lob
    @Column(name = "html_body")
    private String htmlBody;

    // Persistimos map JSON
    @Convert(converter = JsonMapConverter.class)
    @Column(name = "default_vars", columnDefinition = "json")
    private Map<String, Object> defaultVars;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
