package org.acme.resource.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.acme.resource.ClientResource;
import org.acme.service.ClientService;
import org.acme.utils.CustomResource;

import java.util.Arrays;
import java.util.Optional;

import static org.acme.utils.constants.Constants.*;

/**
 * Exposición del servicio de consulta y almacenamiento de clientes.
 * 
 * Esta clase maneja el endpoint /client/{code} que consulta un cliente usando su código
 * y lo persiste si es válido.
 */

@ApplicationScoped
public class ClientResourceImpl extends CustomResource implements ClientResource {

    @Inject
    ClientService clientService;

    private static final String[] VALID_CHANNELS = {"BancoApp", "BancoPersonas", "BancoEmpresas"};

    /**
     * Obtiene la información de un cliente basado en su código y lo almacena en la BD si no existe.
     *
     * @param code    Código único del cliente.
     * @param channel Canal de la solicitud.
     * @return La información del cliente en formato JSON, o un error en caso de fallar.
     */
    
    @Override
    public Response getClient(String code, String channel) {
        // Validación funcional del canal
        return Optional.ofNullable(channel)
                .filter(this::isValidChannel)
                .map(validChannel -> {
                    try{
                        return fetchClient(code);
                    } catch (Exception e) {
                        return response(NOT_FOUND, e.getMessage());
                    }
                })
                .orElseGet(() -> response(BAD_REQUEST, "El canal es requerido o incorrecto"));
    }

    /**
     * Verifica si el canal proporcionado es válido.
     *
     * @param channel Canal de la solicitud.
     * @return true si el canal es válido, false en caso contrario.
     */
    private boolean isValidChannel(String channel) {
        return Arrays.asList(VALID_CHANNELS).contains(channel);
    }

    /**
     * Llama al servicio para obtener el cliente y retorna la respuesta adecuada.
     * Además, persiste el cliente en la base de datos si no existe.
     *
     * @param code Código del cliente.
     * @return La respuesta con la información del cliente o respectivo el error.
     */
    private Response fetchClient(String code) {
        return Optional.ofNullable(clientService.fetchClient(code))
            .map(client -> {
                clientService.persistClientIfNecessary(client);
                return reactiveSuccessResponse(OK, "Cliente encontrado").apply(client);
            })
            .orElseGet(() -> response(NOT_FOUND, "Cliente no Encontrado"));
    }

}
    


