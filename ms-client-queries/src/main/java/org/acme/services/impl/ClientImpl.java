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
        LOG.infof("Buscando cliente en cache: %s", document);

        Context ctx = Vertx.currentContext();

        return getFromCache(document, ctx)
                .map(Optional::ofNullable)
                .chain(optional ->
                    optional.isPresent()
                        ? Uni.createFrom().item(optional)
                        : fetchFromDbAndCache(document, ctx)
                );
    }

    /**
     * Se usa el emitter para asegurar que en todo momento la operación pase por el 
     * event-loop original, evitando perder el contexto.
     * 
     * @return Uni<ClientCache> El cliente obtenido desde la caché.
     * @param document El documento del cliente a buscar.
     * @param ctx El contexto de Vert.x para mantener el event-loop.
     * 
     */
    private Uni<ClientCache> getFromCache(String document, Context ctx) {
        return Uni.createFrom().emitter(em -> {
            cacheService.getAsyncData(document)
                .subscribe().with(
                    item -> ctx.runOnContext(v -> em.complete(item)),
                    err -> ctx.runOnContext(v -> em.fail(err))
                );
        });
    }

    /*
     * Si el cliente no está en la caché, se consulta la base de datos.
     * Si se encuentra, se almacena en la caché antes de devolverlo.
     * 
     * @return Uni<Optional<ClientCache>> El cliente obtenido desde la BD y almacenado en caché.
     * @param document El documento del cliente a buscar.
     * @param ctx El contexto de Vert.x para mantener el event-loop.
     * 
     */
    private Uni<Optional<ClientCache>> fetchFromDbAndCache(String document, Context ctx) {
        LOG.infof("Cache no encontrado para %s, consultando BD", document);

        return Uni.createFrom().emitter(em -> {
            findClientInDb(document)
                .subscribe().with(
                    client -> ctx.runOnContext(v -> {
                        if (client == null) {
                            em.complete(Optional.empty());
                        } else {
                            ClientCache cache = toCache(client);

                            cacheService.putAsyncData(document, cache)
                                .subscribe().with(
                                    x -> ctx.runOnContext(v2 -> em.complete(Optional.of(cache))),
                                    err -> ctx.runOnContext(v2 -> em.fail(err))
                                );
                        }
                    }),
                    err -> ctx.runOnContext(v -> em.fail(err))
                );
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
                client.document,
                client.documentType,
                client.name,
                client.phone,
                client.email,
                client.address,
                client.creditCard
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
        return Client.findByDocument(document);
    }
}
