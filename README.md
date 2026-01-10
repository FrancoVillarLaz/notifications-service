# notifications-service
# 📧 Sistema de Notificaciones Multicanal

Sistema enterprise de gestión de notificaciones con soporte para múltiples canales,
templates dinámicos y programación de envíos.

## 🚀 Características

- ✉️ Soporte multicanal (Email, SMS, Push - extensible)
- 📝 Sistema de templates con variables dinámicas
- ⏰ Programación de notificaciones
- 🔄 Reintentos automáticos
- 📊 Tracking de estado y métricas
- 🌍 Soporte multiidioma
- 💾 Persistencia con PostgreSQL/MySQL

## 🏗️ Arquitectura
```
┌─────────────┐
│ Controller  │
└──────┬──────┘
       │
┌──────▼──────────┐
│ NotificationSvc │──► Factory ──► TemplateProvider
└──────┬──────────┘                      │
       │                                 │
┌──────▼──────┐                    ┌─────▼─────┐
│  Notifiers  │                    │    DB     │
│ (Strategy)  │                    └───────────┘
└─────────────┘
```

## 📡 API Endpoints

### Enviar desde template
```bash
POST /send
{
  "template": "BIENVENIDA",
  "destinatarios": ["user@example.com"],
  "variables": {
    "nombre": "Juan",
    "usuario": "jperez"
  },
  "programarPara": "2026-01-15T10:00:00"  // opcional
}
```

## 🛠️ Stack Tecnológico

- Java 17+
- Spring Boot 3.x
- JPA/Hibernate
- PostgreSQL
- JavaMailSender
- Lombok

## 🔧 Configuración
```yaml
# application.yml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}

notifications:
  scheduler:
    fixed-rate: 60000  # 1 min
```

## 💡 Patrones de Diseño

- **Strategy Pattern**: Para múltiples canales de notificación
- **Factory Pattern**: Creación de notificaciones desde templates
- **Repository Pattern**: Acceso a datos
- **Template Method**: Sistema de plantillas
