package org.acme.utils.filter;

import java.io.IOException;

import org.acme.utils.TraceContext;
import org.jboss.logging.Logger;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;

/**
 * Filtro JAX-RS que captura o genera el correlation ID de las peticiones HTTP
 * y lo establece en el MDC para trazabilidad. También agrega el correlation ID
 * a los headers de respuesta.
 * 
 * @author Felipe Malaver
 * @since 2025-12-26
 * @version 1.0
 */
@Provider
@PreMatching
public class TraceContextFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOG = Logger.getLogger(TraceContextFilter.class);

    /**
     * Filtra las peticiones entrantes para capturar o generar el correlation ID.
     * 
     * @param requestContext El contexto de la petición HTTP.
     * @throws IOException Si ocurre un error al procesar la petición.
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        MultivaluedMap<String, String> headers = requestContext.getHeaders();
        
        // Intentar obtener el correlation ID del header HTTP
        String correlationId = headers.getFirst(TraceContext.CORRELATION_ID_HEADER);
        
        // Si no existe, generar uno nuevo
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = TraceContext.generateAndSetCorrelationId();
            LOG.debugf("Correlation ID generado: %s para la petición %s %s", 
                    correlationId, requestContext.getMethod(), requestContext.getUriInfo().getPath());
        } else {
            TraceContext.setCorrelationId(correlationId);
            LOG.debugf("Correlation ID recibido: %s para la petición %s %s", 
                    correlationId, requestContext.getMethod(), requestContext.getUriInfo().getPath());
        }
        
        // Agregar el correlation ID al contexto de la petición para uso posterior
        requestContext.setProperty(TraceContext.CORRELATION_ID_KEY, correlationId);
    }

    /**
     * Filtra las respuestas salientes para agregar el correlation ID al header.
     * 
     * @param requestContext El contexto de la petición HTTP.
     * @param responseContext El contexto de la respuesta HTTP.
     * @throws IOException Si ocurre un error al procesar la respuesta.
     */
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) 
            throws IOException {
        String correlationId = TraceContext.getCorrelationId();
        
        if (correlationId != null && !correlationId.isEmpty()) {
            // Agregar el correlation ID al header de respuesta
            responseContext.getHeaders().add(TraceContext.CORRELATION_ID_HEADER, correlationId);
            
            LOG.debugf("Correlation ID agregado a la respuesta: %s con status %d", 
                    correlationId, responseContext.getStatus());
        }
        
        // Nota: No limpiamos el MDC aquí porque en aplicaciones reactivas
        // el contexto puede ser compartido entre hilos. La limpieza se hará
        // automáticamente cuando termine el request.
    }
}

