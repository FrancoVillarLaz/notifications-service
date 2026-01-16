package proyect3.notifications.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

/**
 * Definición de variables para un template.
 * Permite documentar y validar las variables esperadas.
 */
@Entity
@Table(name = "template_variable_definitions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateVariableDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private NotificationTemplate template;

    @Column(nullable = false, length = 100)
    private String variableName;

    /**
     * Tipo de dato: STRING, NUMBER, DATE, BOOLEAN, URL
     */
    @Column(nullable = false, length = 50)
    private String variableType;

    @Column(length = 500)
    private String defaultValue;

    @Column(nullable = false)
    @Builder.Default
    private Boolean required = false;

    @Column(length = 1000)
    private String description;
}