package proyect3.notifications.persistence.enums;


import java.util.Set;

public enum Canal {
    EMAIL,
    SMS,
    WHATSAPP,
    PUSH_NOTIFICATION,
    IN_APP_NOTIFICATION;

    public Set<Canal> asSet() {
        return Set.of(this);
    }
}
