package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.acme.dto.ClientDTO;
import org.acme.mapper.ClientMapper;
import org.acme.resource.ExternalClientMockResource;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.acme.model.Client;

import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue.Supplier;

import java.util.Optional;

import static org.acme.utils.constants.Constants.NOT_FOUND;
import static org.acme.utils.constants.Constants.BAD_REQUEST;
import static org.acme.utils.constants.Constants.INTERNAL_SERVER_ERROR;
import static org.acme.utils.constants.Constants.OK;

/**
 * 
 * 
 * @author Felipe Malaver
 * @since 2025-11-26
 * @version 1.0
 */
@ApplicationScoped
public class ClientService {

    @Inject
    ExternalClientMockResource externalClientMockResource;

    @Inject
    ClientMapper clientMapper;

    private final Supplier<Response> codeMissing = () -> Response.status(BAD_REQUEST).entity("Código faltante").build();

    /**
     * Obtiene un cliente usando su código, consumiendo el servicio simulado.
     * 
     * @param code El código del cliente.
     * @return El objet {@link ClientDTO} si se encuentra, o un WebApplicationException en caso de error.
     */
    public ClientDTO fetchClient(String code) {

        String validatedCode = Optional.ofNullable(code)
                                       .filter(c -> !c.isBlank())
                                       .orElseThrow(() -> new WebApplicationException(codeMissing.get()));

        // Llamada al servicio simulado
        Response response = externalClientMockResource.getClient(validatedCode);

        return Optional.of(response)
            .filter(r -> r.getStatus() == OK)
            .map(r -> (ClientDTO) r.getEntity())
            .orElseThrow(() -> Optional.of(response)
                .filter(r -> r.getStatus() == NOT_FOUND)
                .map(r -> new WebApplicationException("El servicio externo respondió: " + r.getEntity(), NOT_FOUND))
            .orElseGet(() -> Optional.of(response)
                .filter(r -> r.getStatus() == BAD_REQUEST)
                .map(r -> new WebApplicationException("El servicio externo respondió: "+ r.getEntity(), BAD_REQUEST))
            .orElse(new WebApplicationException("Error desconocido", INTERNAL_SERVER_ERROR))
        ));
    }

    /**
     * Verifica si el cliente ya está almacenado y, si no es así, lo persiste en la base de datos.
     * 
     * @param clientDTO El DTO del cliente que se obtuvo del servicio.
     * @return El ClientDTO del cliente persistido.
     */
    @Transactional
    public ClientDTO persistClientIfNecessary(ClientDTO clientDTO) {
        Optional.ofNullable(clientDTO)
                .map(client -> {
                    // Verifica si el cliente ya existe en la base de datos
                    Client existingClient = Client.find("document", client.document()).firstResult();
                    if (existingClient == null) {
                        Client clientEntity = clientMapper.toEntity(clientDTO);
                        clientEntity.persist();
                    }
                    return clientDTO;
                })
                .orElseThrow(() -> new WebApplicationException("No se pudo persistir el cliente", INTERNAL_SERVER_ERROR));
        
        return clientDTO;
    }
}