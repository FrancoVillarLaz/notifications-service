package proyect3.notifications.persistence.entities;

import proyect3.notifications.persistence.enums.Canal;
import proyect3.notifications.persistence.enums.EstadoNotification;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "notifications")
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true) // Lombok genera NotificationBuilder con setters fluentes
public class Notification {

    @Id
    private String id;

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String mensaje;

    @ElementCollection
    @CollectionTable(name = "notification_destinatarios", joinColumns = @JoinColumn(name = "notification_id"))
    @Column(name = "destinatario")
    private List<String> destinatarios;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Canal canal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoNotification estado;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, Object> metadata;

    @Column(name = "programar_para")
    private LocalDateTime programarPara;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "mensaje_error", columnDefinition = "TEXT")
    private String mensajeError;

    @Column(name = "intentos")
    private Integer intentos;

    @PrePersist
    public void prePersist() {
        if (this.id == null || this.id.isBlank()) this.id = UUID.randomUUID().toString();
        if (this.fechaCreacion == null) this.fechaCreacion = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = (this.programarPara != null) ? EstadoNotification.PROGRAMADO : EstadoNotification.PENDIENTE;
        }
        if (this.intentos == null) this.intentos = 0;
        if (this.metadata == null) this.metadata = new HashMap<>();
    }

    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    public void marcarComoEnviado() {
        this.estado = EstadoNotification.ENVIADO;
        this.fechaEnvio = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }

    public void marcarComoFallido(String error) {
        this.estado = EstadoNotification.FALLIDO;
        this.mensajeError = error;
        this.fechaActualizacion = LocalDateTime.now();
        this.intentos = (this.intentos == null ? 1 : this.intentos + 1);
    }

    public static class NotificationBuilder {
        public Notification build() {
            // Validaciones mínimas
            if (canal == null) throw new IllegalArgumentException("El canal es obligatorio");
            if (titulo == null || titulo.isBlank()) throw new IllegalArgumentException("El título es obligatorio");
            if (destinatarios == null || destinatarios.isEmpty())
                throw new IllegalArgumentException("Debe haber al menos un destinatario");

            // Defaults
            if (id == null || id.isBlank()) id = UUID.randomUUID().toString();
            if (fechaCreacion == null) fechaCreacion = LocalDateTime.now();
            if (intentos == null) intentos = 0;
            if (metadata == null) metadata = new HashMap<>();
            if (estado == null) {
                estado = (programarPara != null) ? EstadoNotification.PROGRAMADO : EstadoNotification.PENDIENTE;
            }

            return new Notification(
                    id, titulo, mensaje,
                    destinatarios != null ? List.copyOf(destinatarios) : List.of(),
                    canal, estado,
                    metadata, programarPara, fechaCreacion,
                    fechaEnvio, fechaActualizacion, mensajeError, intentos
            );
        }
    }
}
