package org.acme.resource;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Interfaz que define los endpoints REST para la gestión de clientes.
 * @author Felipe Malaver
 * @since 2025-12-09
 * @version 1.0
 */
@Path("/api/v1/clients")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ClientResource {

    /*
     * Endpoint para obtener un cliente por su documento.
     * @return Uni<Response> Respuesta HTTP con el cliente o error si no se encuentra.
     * @param document El documento del cliente a buscar.
     * 
     */
    @GET
    @Path("/{document}")
    Uni<Response> getClientByDocument(@PathParam("document") String document);
}
