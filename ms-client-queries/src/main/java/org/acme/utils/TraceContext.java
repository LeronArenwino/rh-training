package org.acme.utils;

import java.util.UUID;

import org.jboss.logging.MDC;

/**
 * Utilidad para manejar el contexto de trazabilidad (correlation ID) usando MDC.
 * Permite almacenar y recuperar el correlation ID que identifica una petición
 * a través de toda la aplicación.
 * 
 * @author Felipe Malaver
 * @since 2025-12-26
 * @version 1.0
 */
public class TraceContext {

    /**
     * Clave utilizada en el MDC para almacenar el correlation ID.
     */
    public static final String CORRELATION_ID_KEY = "correlationId";

    /**
     * Header HTTP estándar para el correlation ID.
     */
    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";

    /**
     * Establece el correlation ID en el MDC.
     * 
     * @param correlationId El correlation ID a establecer.
     */
    public static void setCorrelationId(String correlationId) {
        if (correlationId != null && !correlationId.isEmpty()) {
            MDC.put(CORRELATION_ID_KEY, correlationId);
        }
    }

    /**
     * Obtiene el correlation ID del MDC.
     * 
     * @return El correlation ID actual o null si no está establecido.
     */
    public static String getCorrelationId() {
        Object value = MDC.get(CORRELATION_ID_KEY);
        return value != null ? value.toString() : null;
    }

    /**
     * Genera un nuevo correlation ID (UUID) y lo establece en el MDC.
     * 
     * @return El correlation ID generado.
     */
    public static String generateAndSetCorrelationId() {
        String correlationId = UUID.randomUUID().toString();
        setCorrelationId(correlationId);
        return correlationId;
    }

    /**
     * Limpia el correlation ID del MDC.
     */
    public static void clearCorrelationId() {
        MDC.remove(CORRELATION_ID_KEY);
    }

    /**
     * Obtiene el correlation ID del MDC o genera uno nuevo si no existe.
     * 
     * @return El correlation ID actual o uno nuevo generado.
     */
    public static String getOrGenerateCorrelationId() {
        String correlationId = getCorrelationId();
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = generateAndSetCorrelationId();
        }
        return correlationId;
    }

    /**
     * Formatea un mensaje de log incluyendo el correlation ID.
     * 
     * @param message El mensaje original.
     * @return El mensaje formateado con el correlation ID.
     */
    public static String formatWithCorrelationId(String message) {
        String correlationId = getCorrelationId();
        if (correlationId != null && !correlationId.isEmpty()) {
            return String.format("[%s] %s", correlationId, message);
        }
        return message;
    }
}

