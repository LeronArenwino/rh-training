package org.acme.services.impl;

import java.util.Optional;

import org.acme.services.CacheService;
import org.acme.services.ClientService;
import org.acme.model.Client;
import org.acme.model.ClientCache;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import io.quarkus.hibernate.reactive.panache.common.WithSession;

import org.acme.utils.TraceContext;
import org.jboss.logging.Logger;
import io.vertx.core.Vertx;
import io.vertx.core.Context;

/**
 * Clase que implementa la lógica del servicio de Cliente, incluyendo la 
 * lógica de caché con Infinispan. 
 * 
 * Se intenta primero obtener los datos desde la caché; si no se encuentran,
 * se consulta la base de datos y se almacena el resultado en la caché para
 * futuras consultas.
 * 
 * La gestión del contexto de Vert.x se maneja cuidadosamente para asegurar
 * que las operaciones asíncronas interactúen correctamente con el event-loop
 * original.
 * 
 * @author Felipe Malaver
 * @since 2025-12-09
 * @version 1.0
 */

@ApplicationScoped
public class ClientImpl implements ClientService {

    private static final Logger LOG = Logger.getLogger(ClientImpl.class);

    private final CacheService cacheService;

    @Inject
    public ClientImpl(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    private Context safeContext(Context ctx) {
        return ctx != null ? ctx : Vertx.vertx().getOrCreateContext();
    }

    /*
     * Primero intenta obtener el cliente desde la caché. Si no está presente,
     * consulta la base de datos y almacena el resultado en la caché.
     * 
     * @return Uni<Optional<ClientCache>> El cliente encontrado o vacío si no existe.
     * @param document El documento del cliente a buscar.
     * 
     * Tener en cuenta que con Mutiny, es necesario mantener el contexto original
     * dado que las operaciones asíncronas pueden ejecutarse en diferentes hilos.
    */
    @Override
    public Uni<Optional<ClientCache>> getClient(String document) {
        String correlationId = TraceContext.getOrGenerateCorrelationId();
        LOG.infof("[%s] Buscando cliente en cache: %s", correlationId, document);

        Context ctx = safeContext(Vertx.currentContext());

        // Capturar el contexto de Vert.x al inicio para asegurar que las operaciones de BD se ejecuten en él
        Context vertxContext = safeContext(ctx);
        
        return getFromCache(document)
                .map(Optional::ofNullable)
                .chain(optional ->
                    optional.isPresent()
                        ? Uni.createFrom().item(optional)
                        : fetchFromDbAndCache(document, vertxContext)
                );
    }

    /**
     * Obtiene el cliente desde la caché de forma reactiva.
     * 
     * @return Uni<ClientCache> El cliente obtenido desde la caché.
     * @param document El documento del cliente a buscar.
     * 
     */
    private Uni<ClientCache> getFromCache(String document) {
        String correlationId = TraceContext.getOrGenerateCorrelationId();
        LOG.debugf("[%s] Obteniendo cliente desde caché para documento: %s", correlationId, document);
        
        return cacheService.getAsyncData(document)
            .onItem().invoke(cache -> {
                if (cache != null) {
                    LOG.debugf("[%s] Cliente encontrado en caché para documento: %s", correlationId, document);
                } else {
                    LOG.debugf("[%s] Cliente no encontrado en caché para documento: %s", correlationId, document);
                }
            })
            .onFailure().invoke(err -> 
                LOG.errorf(err, "[%s] Error al obtener cliente desde caché para documento: %s - Error: %s", 
                        correlationId, document, err.getMessage())
            );
    }

    /*
     * Si el cliente no está en la caché, se consulta la base de datos.
     * Si se encuentra, se almacena en la caché antes de devolverlo.
     * Implementado de forma completamente reactiva usando el paradigma de Mutiny.
     * 
     * @return Uni<Optional<ClientCache>> El cliente obtenido desde la BD y almacenado en caché.
     * @param document El documento del cliente a buscar.
     * @param ctx El contexto de Vert.x para mantener el event-loop (no usado directamente, pero mantenido para compatibilidad).
     * 
     */
    private Uni<Optional<ClientCache>> fetchFromDbAndCache(String document, Context ctx) {
        String correlationId = TraceContext.getOrGenerateCorrelationId();
        Context safeCtx = safeContext(ctx);
        
        LOG.infof("[%s] Iniciando consulta a BD para documento: %s", correlationId, document);
        LOG.debugf("[%s] Contexto Vert.x: %s (isEventLoopContext: %s)", 
                correlationId, safeCtx, safeCtx != null && safeCtx.isEventLoopContext());
        
        // Usar emitter para ejecutar la llamada a findClientInDb dentro del contexto de Vert.x
        // Esto es necesario porque @WithSession requiere un contexto de Vert.x activo
        // cuando se intercepta la llamada al método findClientInDb
        return Uni.createFrom().<Client>emitter(em -> {
            if (safeCtx != null && Vertx.currentContext() != safeCtx) {
                // Si no estamos en el contexto correcto, ejecutar en él
                safeCtx.runOnContext(v -> 
                    findClientInDb(document)
                        .subscribe().with(em::complete, em::fail)
                );
            } else {
                // Si ya estamos en el contexto correcto, ejecutar directamente
                findClientInDb(document)
                    .subscribe().with(em::complete, em::fail);
            }
        })
            .onFailure().invoke(err -> {
                LOG.errorf(err, "[%s] Error al consultar BD para documento: %s - Tipo: %s - Mensaje: %s", 
                        correlationId, document, err.getClass().getSimpleName(), err.getMessage());
                if (err.getCause() != null) {
                    LOG.errorf("[%s] Causa del error: %s - %s", 
                            correlationId, err.getCause().getClass().getSimpleName(), err.getCause().getMessage());
                }
            })
            .onItem().transform(client -> {
                if (client == null) {
                    LOG.warnf("[%s] Cliente NO encontrado en BD para documento: %s", correlationId, document);
                    return Optional.<ClientCache>empty();
                }
                
                LOG.infof("[%s] Cliente encontrado en BD para documento: %s - Nombre: %s", 
                        correlationId, document, client.getName());
                ClientCache cache = toCache(client);
                LOG.debugf("[%s] Cliente convertido a ClientCache para documento: %s", 
                        correlationId, document);
                return Optional.of(cache);
            })
            .chain(optional -> {
                if (optional.isEmpty()) {
                    return Uni.createFrom().item(optional);
                }
                
                ClientCache cache = optional.get();
                LOG.infof("[%s] Almacenando cliente en caché para documento: %s", correlationId, document);
                return cacheService.putAsyncData(document, cache)
                    .onItem().invoke(x -> 
                        LOG.infof("[%s] Cliente almacenado exitosamente en caché para documento: %s", 
                                correlationId, document)
                    )
                    .onFailure().invoke(err -> 
                        LOG.errorf(err, "[%s] Error al almacenar cliente en caché para documento: %s - Error: %s", 
                                correlationId, document, err.getMessage())
                    )
                    .replaceWith(optional);
            });
    }

    /*
     * Convierte una entidad Client a ClientCache para almacenarla en la caché.
     * @return ClientCache El cliente convertido para caché.
     * @param client La entidad Client a convertir.
     *  
     */
    private ClientCache toCache(Client client) {
        return new ClientCache(
                client.getDocument(),
                client.getDocumentType(),
                client.getName(),
                client.getPhone(),
                client.getEmail(),
                client.getAddress(),
                client.getCreditCard()
        );
    }

    /*
     * Consulta la base de datos para encontrar el cliente por su documento.
     * @return Uni<Client> El cliente encontrado en la base de datos.
     * @param document El documento del cliente a buscar.
     * @WithSession Garantiza que la operación se realice dentro de una sesión de Hibernate Reactiva.
     *  
     */
    @WithSession
    public Uni<Client> findClientInDb(String document) {
        String correlationId = TraceContext.getOrGenerateCorrelationId();
        LOG.infof("[%s] Ejecutando consulta Hibernate Reactive para documento: %s", correlationId, document);
        LOG.debugf("[%s] Método Client.findByDocument invocado", correlationId);
        
        Uni<Client> result = Client.findByDocument(document);
        
        LOG.debugf("[%s] Uni<Client> creado para documento: %s, verificando si la consulta se ejecutará", 
                correlationId, document);
        
        return result
            .onItem().invoke(client -> {
                if (client != null) {
                    LOG.infof("[%s] Consulta Hibernate Reactive exitosa - Cliente encontrado: documento=%s, nombre=%s", 
                            correlationId, document, client.getName());
                } else {
                    LOG.warnf("[%s] Consulta Hibernate Reactive completada pero cliente es null para documento: %s", 
                            correlationId, document);
                }
            })
            .onFailure().invoke(err -> {
                LOG.errorf(err, "[%s] Error en consulta Hibernate Reactive para documento: %s - Tipo: %s - Mensaje: %s", 
                        correlationId, document, err.getClass().getSimpleName(), err.getMessage());
                if (err.getCause() != null) {
                    LOG.errorf("[%s] Causa del error en Hibernate: %s - %s", 
                            correlationId, err.getCause().getClass().getSimpleName(), err.getCause().getMessage());
                }
            });
    }
}
