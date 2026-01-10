package franco.notifications.persistence.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Map;

@Converter(autoApply = false)
public class JsonMapConverter implements AttributeConverter<Map<String,Object>, String> {

    private static final ObjectMapper M = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        try {
            return attribute == null ? null : M.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new IllegalStateException("Error serializing JSON map", e);
        }
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        try {
            return dbData == null ? null : M.readValue(dbData, new TypeReference<Map<String,Object>>() {});
        } catch (Exception e) {
            throw new IllegalStateException("Error deserializing JSON map", e);
        }
    }
}
