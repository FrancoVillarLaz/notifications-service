package proyect3.notifications.persistence.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import proyect3.notifications.persistence.enums.Canal;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Entidad que representa un Template de Notificación.
 * Contiene el contenido específico que se renderizará dentro de un Layout.
 * Soporta múltiples canales (EMAIL, SMS, PUSH, etc.)
 */
@Entity
@Table(name = "notification_templates",
        indexes = {
                @Index(name = "idx_template_code", columnList = "code"),
                @Index(name = "idx_template_active", columnList = "active")
        })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * Código único del template: BIENVENIDA, RECUPERAR_PASSWORD, etc.
     * Se usa para buscar el template desde el código.
     */
    @Column(nullable = false, unique = true, length = 100)
    private String code;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 500)
    private String description;

    /**
     * Relación con el Layout que envuelve este template.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "layout_id")
    private NotificationLayout layout;

    /**
     * Template del asunto/título (soporta variables Mustache).
     * Ej: "¡Bienvenido a {{nombreApp}}!"
     */
    @Column(nullable = false, length = 500)
    private String subjectTemplate;

    /**
     * Template del cuerpo en HTML (sin layout, solo el contenido).
     * Se insertará en el {{CONTENT}} del layout.
     * Ej: "<p>Hola {{nombre}}, tus credenciales son...</p>"
     */
    @Column(columnDefinition = "TEXT")
    private String bodyHtmlTemplate;

    /**
     * Template del cuerpo en texto plano (para emails sin HTML o SMS).
     */
    @Column(columnDefinition = "TEXT")
    private String bodyTextTemplate;

    /**
     * Variables requeridas para renderizar el template.
     * JSON array: ["nombre", "usuario", "password"]
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    @Builder.Default
    private List<String> requiredVariables = new ArrayList<>();

    /**
     * Canales soportados por este template.
     * Un template puede estar diseñado para EMAIL, otro para SMS, etc.
     */
    @ElementCollection(targetClass = Canal.class)
    @CollectionTable(name = "template_supported_channels",
            joinColumns = @JoinColumn(name = "template_id"))
    @Column(name = "channel")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Set<Canal> supportedChannels = new HashSet<>();

    /**
     * Variables con valores por defecto.
     * JSON object: {"nombreApp": "Inncome", "urlLogin": "https://..."}
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    @Builder.Default
    private java.util.Map<String, Object> defaultVariables = new java.util.HashMap<>();

    /**
     * Versión del template para control de cambios.
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer version = 1;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Relación bidireccional con definiciones de variables.
     */
    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TemplateVariableDefinition> variableDefinitions = new ArrayList<>();

    /**
     * Verifica si el template soporta un canal específico.
     */
    public boolean supportsChannel(Canal canal) {
        return supportedChannels.contains(canal);
    }

    /**
     * Verifica si todas las variables requeridas están presentes.
     */
    public boolean hasAllRequiredVariables(java.util.Map<String, Object> providedVariables) {
        if (requiredVariables == null || requiredVariables.isEmpty()) {
            return true;
        }
        return providedVariables.keySet().containsAll(requiredVariables);
    }
}