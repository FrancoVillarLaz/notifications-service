package franco.notifications.controller.advice;


import franco.notifications.exception.NotificationException;
import franco.notifications.persistence.dto.ApiResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotificationException.class)
    public ResponseEntity<ApiResponseDTO> handleNotificationException(NotificationException e) {
        log.error("NotificationException capturada", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Error en el servicio de notificaciones: " + e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponseDTO> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("Argumento inválido", e);
        return ResponseEntity
                .badRequest()
                .body(ApiResponseDTO.error("Datos inválidos: " + e.getMessage()));
    }

    /**
     * Manejador para @Valid. Captura errores de validación de DTOs.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDTO> handleValidationException(MethodArgumentNotValidException e) {
        String errores = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("Error de validación: {}", errores);
        return ResponseEntity
                .badRequest()
                .body(ApiResponseDTO.error("Error de validación: " + errores));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO> handleGenericException(Exception e) {
        log.error("Error inesperado en el controlador", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Error interno del servidor: " + e.getMessage()));
    }
}