package co.com.training.filter;

import java.util.UUID;

import org.jboss.logging.MDC;
import org.jboss.logging.Logger;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

/**
 * Filter for managing request correlation IDs for distributed tracing.
 * 
 * <p>This filter implements request tracing by:
 * <ul>
 *   <li>Extracting a correlation ID from the HTTP header "X-Correlation-ID" if present</li>
 *   <li>Generating a new UUID-based correlation ID if not present in the request</li>
 *   <li>Storing the correlation ID in the MDC (Mapped Diagnostic Context) for logging</li>
 *   <li>Adding the correlation ID to the response header "X-Correlation-ID"</li>
 *   <li>Cleaning up the MDC after request processing</li>
 * </ul>
 * </p>
 * 
 * <p>The correlation ID will be automatically included in all log messages
 * if the logging pattern is configured to include MDC values.</p>
 * 
 * <p>This filter is automatically registered by JAX-RS due to the {@link Provider}
 * annotation and will be applied to all REST endpoints.</p>
 * 
 * @author Francisco Dueñas
 * @since 1.0.1
 */
@Provider
@Priority(Priorities.USER)
public class CorrelationIdFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOG = Logger.getLogger(CorrelationIdFilter.class);
    
    /**
     * HTTP header name for correlation ID in requests and responses.
     */
    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    
    /**
     * MDC key for storing the correlation ID in the logging context.
     */
    private static final String MDC_CORRELATION_ID_KEY = "correlationId";

    /**
     * Processes incoming requests to extract or generate a correlation ID.
     * 
     * <p>This method:
     * <ol>
     *   <li>Checks for an existing "X-Correlation-ID" header in the request</li>
     *   <li>If found, uses that value; otherwise generates a new UUID</li>
     *   <li>Stores the correlation ID in the MDC for logging</li>
     *   <li>Logs the incoming request with the correlation ID</li>
     * </ol>
     * </p>
     * 
     * @param requestContext The request context containing HTTP request information
     */
    @Override
    public void filter(ContainerRequestContext requestContext) {
        String correlationId = extractOrGenerateCorrelationId(requestContext);
        
        // Store in MDC for logging - must be set before any logging
        MDC.put(MDC_CORRELATION_ID_KEY, correlationId);
        
        // Log incoming request (correlation ID automatically included via MDC in log pattern)
        String method = requestContext.getMethod();
        String path = requestContext.getUriInfo().getPath();
        LOG.infof("Incoming request: %s %s", method, path);
    }

    /**
     * Processes outgoing responses to add the correlation ID header and clean up MDC.
     * 
     * <p>This method:
     * <ol>
     *   <li>Retrieves the correlation ID from MDC</li>
     *   <li>Adds it to the response header "X-Correlation-ID"</li>
     *   <li>Logs the outgoing response with status code and correlation ID</li>
     *   <li>Cleans up the MDC to prevent memory leaks</li>
     * </ol>
     * </p>
     * 
     * @param requestContext The request context
     * @param responseContext The response context containing HTTP response information
     */
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        Object correlationIdObj = MDC.get(MDC_CORRELATION_ID_KEY);
        String correlationId = correlationIdObj != null ? correlationIdObj.toString() : null;
        
        if (correlationId != null) {
            // Add correlation ID to response header
            responseContext.getHeaders().add(CORRELATION_ID_HEADER, correlationId);
            
            // Log outgoing response (correlation ID automatically included via MDC in log pattern)
            int statusCode = responseContext.getStatus();
            String method = requestContext.getMethod();
            String path = requestContext.getUriInfo().getPath();
            LOG.infof("Outgoing response: %s %s - Status: %d", method, path, statusCode);
        }
        
        // Clean up MDC to prevent memory leaks
        MDC.remove(MDC_CORRELATION_ID_KEY);
    }

    /**
     * Extracts the correlation ID from the request header or generates a new one.
     * 
     * <p>If the request contains an "X-Correlation-ID" header, that value is used.
     * Otherwise, a new UUID is generated. This allows for distributed tracing
     * across multiple services.</p>
     * 
     * @param requestContext The request context
     * @return The correlation ID to use for this request
     */
    private String extractOrGenerateCorrelationId(ContainerRequestContext requestContext) {
        String correlationId = requestContext.getHeaderString(CORRELATION_ID_HEADER);
        
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
            LOG.debugf("Generated new correlation ID");
        } else {
            LOG.debugf("Using correlation ID from request header");
        }
        
        return correlationId;
    }
}

