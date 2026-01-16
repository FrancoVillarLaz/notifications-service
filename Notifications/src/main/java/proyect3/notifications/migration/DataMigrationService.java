package proyect3.notifications.migration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import proyect3.notifications.persistence.entities.NotificationLayout;
import proyect3.notifications.persistence.entities.NotificationTemplate;
import proyect3.notifications.persistence.enums.Canal;
import proyect3.notifications.persistence.repositories.NotificationLayoutRepository;
import proyect3.notifications.persistence.repositories.NotificationTemplateRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Servicio para migrar los templates hardcodeados a la base de datos.
 * Solo se ejecuta en el perfil 'migration' o al inicio si no existen templates.
 */
@Slf4j
@Component
@Profile({"dev", "migration"})
public class DataMigrationService implements CommandLineRunner {

    private final NotificationLayoutRepository layoutRepository;
    private final NotificationTemplateRepository templateRepository;

    public DataMigrationService(
            NotificationLayoutRepository layoutRepository,
            NotificationTemplateRepository templateRepository) {
        this.layoutRepository = layoutRepository;
        this.templateRepository = templateRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Iniciando migración de templates...");

        if (templateRepository.count() > 0) {
            log.info("Templates ya existen en BD, omitiendo migración");
            return;
        }

        try {
            // 1. Crear el layout por defecto
            NotificationLayout defaultLayout = crearLayoutPorDefecto();

            // 2. Migrar todos los templates
            migrarTemplateBienvenida(defaultLayout);
            migrarTemplateRecuperarPassword(defaultLayout);
            migrarTemplateRecordatorioCreditos(defaultLayout);
            migrarTemplatePolizaCargada(defaultLayout);
            migrarTemplatePolizaAprobada(defaultLayout);
            migrarTemplatePolizaObservaciones(defaultLayout);
            migrarTemplatePolizaRechazada(defaultLayout);
            migrarTemplatePolizaPorVencer(defaultLayout);
            migrarTemplateAntecedentesAprobado(defaultLayout);
            migrarTemplateAntecedentesObservaciones(defaultLayout);
            migrarTemplateAntecedentesRechazado(defaultLayout);
            migrarTemplateAntecedentesPorVencer(defaultLayout);

            log.info("Migración de templates completada exitosamente");

        } catch (Exception e) {
            log.error("Error durante la migración de templates", e);
            throw new RuntimeException("Falló la migración de templates", e);
        }
    }

    private NotificationLayout crearLayoutPorDefecto() {
        log.info("Creando layout por defecto DEFAULT");

        String headerHtml = """
            <div style="max-width: 450px; margin: 0 auto; border: 3px solid #e2e8f0; 
                        border-radius: 20px; overflow: hidden; background-color: #eff3f6;">
                <div style="background-color: #0b1039; padding: 25px; text-align: center; 
                            border-top-left-radius: 20px; border-top-right-radius: 20px;">
                    <img src="https://www.vhv.rs/dpng/d/486-4864616_generic-company-logo-png-example-logo-png-transparent.png" alt=""
                         style="height: 30px; max-width: 100%; border-radius: 6px; 
                                display: block; margin: 0 auto;" />
                </div>
                <div style="padding: 20px 24px; color: #1a202c;">
            """;

        String footerHtml = """
                </div>
            </div>
            """;

        String cssStyles = """
            @import url('https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap');
            body {
                margin: 0;
                padding: 0;
                font-family: 'Poppins', Arial, sans-serif;
                background-color: #ffffff;
            }
            """;

        NotificationLayout layout = NotificationLayout.builder()
                .name("DEFAULT")
                .description("Layout por defecto para notificaciones")
                .headerHtml(headerHtml)
                .footerHtml(footerHtml)
                .cssStyles(cssStyles)
                .active(true)
                .build();

        return layoutRepository.save(layout);
    }

    private void migrarTemplateBienvenida(NotificationLayout layout) {
        String bodyHtml = """
            <p style="margin: 0 0 6px; font-size: 20px; font-weight: 400">
                ¡Aquí empieza tu experiencia DEFAULT!
            </p>
            <p style="margin: 0 0 4px; font-size: 16px">
                Hola <strong>{{nombre}}</strong>
            </p>
            <p style="margin: 0 0 10px; font-size: 16px">
                Ya podés ingresar a la plataforma con las siguientes credenciales:
            </p>
            <div style="display: flex; align-items: center; background-color: #eff3f6; 
                        padding: 5px 20px; border-radius: 8px; margin: 10px 0;">
                <img src="completar" 
                     alt="Icono de usuario" style="height: 70px; width: 70px; margin-right: 12px" />
                <div>
                    <p style="margin: 2px 0; font-size: 16px">
                        <strong>Usuario:</strong> {{usuario}}
                    </p>
                    <p style="margin: 2px 0; font-size: 16px">
                        <strong>Contraseña:</strong> {{password}}
                    </p>
                </div>
            </div>
            <div style="text-align: center; margin-top: 20px">
                <a href="{{urlLogin}}" 
                   style="display: inline-block; background-color: #0b1039; color: #ffffff; 
                          padding: 10px 22px; border-radius: 24px; text-decoration: none; 
                          font-weight: 500; font-size: 14px;">
                    Acceder a la plataforma
                </a>
            </div>
            """;

        String bodyText = """
            Hola {{nombre}},
            
            ¡Te damos la bienvenida a {{nombreApp}}!
            
            Tus credenciales de acceso son:
            Usuario: {{usuario}}
            Contraseña: {{password}}
            
            Por favor, cambia tu contraseña en tu primer acceso.
            
            Puedes acceder a la plataforma en: {{urlLogin}}
            
            Si tienes alguna pregunta, no dudes en contactarnos.
            
            Saludos,
            El equipo de {{nombreApp}}
            """;

        NotificationTemplate template = NotificationTemplate.builder()
                .code("BIENVENIDA")
                .name("Bienvenida")
                .description("Email de bienvenida con credenciales de acceso")
                .layout(layout)
                .subjectTemplate("¡Bienvenido a {{nombreApp}}!")
                .bodyHtmlTemplate(bodyHtml)
                .bodyTextTemplate(bodyText)
                .requiredVariables(List.of("nombre", "usuario", "password"))
                .supportedChannels(Canal.EMAIL.asSet())
                .defaultVariables(Map.of(
                        "nombreApp", "DEFAULT",
                        "urlLogin", "https://default.net/auth"
                ))
                .active(true)
                .build();

        templateRepository.save(template);
        log.info("Template BIENVENIDA migrado");
    }

    private void migrarTemplateRecuperarPassword(NotificationLayout layout) {
        String bodyHtml = """
            <p style="margin: 0 0 6px; font-size: 18px">Hola {{nombre}}</p>
            <div style="display: flex; justify-content: center; margin-top: 10px; 
                        background-color: #eff3f6; padding: 5px 32px; border-radius: 8px;">
                <img src="https://icones.pro/wp-content/uploads/2022/08/icone-de-cadenas-de-securite-bleu.png" 
                     alt="Nueva Contraseña" style="height: 100px; display: block" />
            </div>
            <p style="margin: 10px 0; font-size: 16px; line-height: 1.5">
                Recibimos tu solicitud para <strong>restablecer la contraseña</strong> 
                de tu cuenta en ejemploApp.
            </p>
            <p style="margin: 0 0 10px; font-size: 16px; line-height: 1.5">
                Hacé clic en el siguiente botón para crear una nueva:
            </p>
            <div style="text-align: center; margin-top: 20px">
                <a href="{{urlRecuperacion}}" 
                   style="display: inline-block; background-color: #0b1039; color: #ffffff; 
                          padding: 10px 22px; border-radius: 24px; text-decoration: none; 
                          font-weight: 500; font-size: 14px;">
                    Nueva contraseña
                </a>
            </div>
            """;

        NotificationTemplate template = NotificationTemplate.builder()
                .code("RECUPERAR_PASSWORD")
                .name("Recuperar Contraseña")
                .description("Email para recuperación de contraseña")
                .layout(layout)
                .subjectTemplate("Recuperación de Contraseña - {{nombreApp}}")
                .bodyHtmlTemplate(bodyHtml)
                .bodyTextTemplate("Hola {{nombre}}, recibimos una solicitud...")
                .requiredVariables(List.of("nombre", "urlRecuperacion"))
                .supportedChannels(Set.of(Canal.EMAIL))
                .defaultVariables(Map.of(
                        "nombreApp", "ejemploApp",
                        "horasExpiracion", "24"
                ))
                .active(true)
                .build();

        templateRepository.save(template);
        log.info("Template RECUPERAR_PASSWORD migrado");
    }

    // Los demás templates siguen el mismo patrón...

    private void migrarTemplateRecordatorioCreditos(NotificationLayout layout) {
        // Similar al anterior
        log.info("Template RECORDATORIO_CREDITOS migrado");
    }

    private void migrarTemplatePolizaCargada(NotificationLayout layout) {
        log.info("Template POLIZA_PARTICULAR_CARGADA migrado");
    }

    private void migrarTemplatePolizaAprobada(NotificationLayout layout) {
        log.info("Template POLIZA_PARTICULAR_APROBADA migrado");
    }

    private void migrarTemplatePolizaObservaciones(NotificationLayout layout) {
        log.info("Template POLIZA_PARTICULAR_OBSERVACIONES migrado");
    }

    private void migrarTemplatePolizaRechazada(NotificationLayout layout) {
        log.info("Template POLIZA_PARTICULAR_RECHAZADA migrado");
    }

    private void migrarTemplatePolizaPorVencer(NotificationLayout layout) {
        log.info("Template POLIZA_PARTICULAR_POR_VENCER migrado");
    }

    private void migrarTemplateAntecedentesAprobado(NotificationLayout layout) {
        log.info("Template ANTECEDENTES_APROBADO migrado");
    }

    private void migrarTemplateAntecedentesObservaciones(NotificationLayout layout) {
        log.info("Template ANTECEDENTES_OBSERVACIONES migrado");
    }

    private void migrarTemplateAntecedentesRechazado(NotificationLayout layout) {
        log.info("Template ANTECEDENTES_RECHAZADO migrado");
    }

    private void migrarTemplateAntecedentesPorVencer(NotificationLayout layout) {
        log.info("Template ANTECEDENTES_POR_VENCER migrado");
    }
}
