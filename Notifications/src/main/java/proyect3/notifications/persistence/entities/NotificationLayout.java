package proyect3.notifications.persistence.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa el Layout común para múltiples templates.
 * Contiene el HTML estructural (header, footer) y estilos CSS.
 * Permite cambiar la apariencia de todos los templates modificando un solo layout.
 */
@Entity
@Table(name = "notification_layouts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationLayout {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true, length = 100)
    private String name; // Ej: "INNCOME_DEFAULT", "INNCOME_PROMOTIONAL"

    @Column(length = 500)
    private String description;

    /**
     * HTML del encabezado que se repetirá en todos los templates.
     * Incluye logo, estilos inline, y estructura superior.
     */
    @Column(columnDefinition = "TEXT")
    private String headerHtml;

    /**
     * HTML del pie de página común.
     */
    @Column(columnDefinition = "TEXT")
    private String footerHtml;

    /**
     * CSS que se aplicará globalmente al template.
     * Se puede incluir como <style> o inline.
     */
    @Column(columnDefinition = "TEXT")
    private String cssStyles;

    /**
     * Placeholder donde se insertará el contenido específico del template.
     * Por defecto: {{CONTENT}}
     */
    @Column(nullable = false)
    @Builder.Default
    private String contentPlaceholder = "{{CONTENT}}";

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @OneToMany(mappedBy = "layout", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<NotificationTemplate> templates = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Renderiza el layout completo insertando el contenido proporcionado.
     * @param content El HTML del contenido específico del template
     * @return El HTML completo con header + content + footer
     */
    public String render(String content) {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html><html><head>");
        html.append("<meta charset=\"UTF-8\">");
        html.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");

        if (cssStyles != null && !cssStyles.isBlank()) {
            html.append("<style>").append(cssStyles).append("</style>");
        }

        html.append("</head><body>");

        if (headerHtml != null) {
            html.append(headerHtml);
        }

        html.append(content);

        if (footerHtml != null) {
            html.append(footerHtml);
        }

        html.append("</body></html>");

        return html.toString();
    }
}
