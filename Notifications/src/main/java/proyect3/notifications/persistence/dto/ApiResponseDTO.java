package proyect3.notifications.persistence.dto;


import java.util.Map;

/**
 * DTO estandarizado para respuestas de la API
 */
public class ApiResponseDTO {
    private String status;
    private String message;
    private Map<String, Object> data;

    private ApiResponseDTO(String status, String message, Map<String, Object> data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static ApiResponseDTO success(String message, Map<String, Object> data) {
        return new ApiResponseDTO("success", message, data);
    }

    public static ApiResponseDTO error(String message) {
        return new ApiResponseDTO("error", message, null);
    }

    // Getters
    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public Map<String, Object> getData() { return data; }
}
