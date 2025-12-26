package org.acme.services.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.acme.utils.TraceContext;
import org.jboss.logging.Logger;

import static org.acme.utils.constants.Constants.CACHE_REMOTE_NAME;

import org.acme.model.ClientCache;
import org.acme.services.CacheService;
import org.infinispan.client.hotrod.RemoteCache;

import io.smallrye.mutiny.Uni;
import io.quarkus.infinispan.client.Remote;


/**
 * Clase que implementa la lógica del servicio de caché utilizando Infinispan.
 * 
 * @author Felipe Malaver
 * @since 2025-12-09
 * @version 1.0
 */
@ApplicationScoped
public class CacheImpl implements CacheService {

    private static final Logger LOG = Logger.getLogger(CacheImpl.class);

    private final RemoteCache<String, ClientCache> cache;

    @Inject
    public CacheImpl(@Remote(CACHE_REMOTE_NAME) RemoteCache<String, ClientCache> cache) {
        this.cache = cache;
    }

    /*
     * Consulta de datos asíncrona en la caché de Infinispan.
     * @return Uni<ClientCache> El cliente almacenado en caché.
     * @param id El ID del cliente a buscar.
     * 
     */
    @Override
    public Uni<ClientCache> getAsyncData(String id) {
        String correlationId = TraceContext.getOrGenerateCorrelationId();
        LOG.infof("[%s] Consultado datos en RH DataGrid para el ID: %s", correlationId, id);
        return Uni.createFrom().completionStage(cache.getAsync(id));
    }

    /*
     * Almacenamiento de datos asíncrono en la caché de Infinispan.
     * @return Uni<ClientCache> El cliente almacenado en caché.
     * @param id El ID del cliente a almacenar.
     * @param client El cliente a almacenar en caché.
     * 
     */
    @Override
    public Uni<ClientCache> putAsyncData(String id, ClientCache client) {
        String correlationId = TraceContext.getOrGenerateCorrelationId();
        LOG.infof("[%s] Creando un Cliente en RH DataGrid con el ID: %s", correlationId, id);

        return Uni.createFrom()
            .completionStage(cache.putAsync(id, client, 0L, java.util.concurrent.TimeUnit.MILLISECONDS, 0L,
                        java.util.concurrent.TimeUnit.MILLISECONDS))
            .replaceWith(client);
    }
}
