package org.acme.resource.impl;

import org.acme.dto.AddressDTO;
import org.acme.dto.ClientDTO;

import jakarta.enterprise.context.ApplicationScoped;

import jakarta.ws.rs.core.Response;
import org.acme.resource.ExternalClientMockResource;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static org.acme.utils.constants.Constants.NOT_FOUND;
import static org.acme.utils.constants.Constants.BAD_REQUEST;

/**
 * Implementación del servicio externo simulado para consultar un cliente por código.
 * 
 * @author Felipe Malaver
 * @since 2025-11-26
 * @version 1.0
 */
@ApplicationScoped
public class ExternalClientMockResourceImpl implements ExternalClientMockResource {

    private static final Map<String, ClientDTO> OK_CLIENTS = Map.of(
        "1073170490", new ClientDTO("Juan Pérez", "1073170490", "Gerente de Compras", "juan.perez@empresaxyz.com", "+18095551234", "+18095555678", List.of(new AddressDTO("Santo Domingo", "RD", "10102"))),
        "1073514698", new ClientDTO("María López", "1073514698", "Analista Senior", "maria.lopez@corp.com", "+18095550000", "+18095551111", List.of(new AddressDTO("Santiago", "RD", "51000")))
    );

    private static final Map<String, Supplier<Response>> ERROR_CODES = Map.of(
        "000000", () -> Response.status(BAD_REQUEST).entity("Código incorrecto - CO_001").build(),
        "123456", () -> Response.status(NOT_FOUND).entity("Código correcto con cliente no encontrado - CO_002").build()
    );

    /**
	 * Genera la simulación de respuesta del servicio externo.
	 *
	 * @param code       Codigo a consultar en la base de datos.
	 * @return respuesta reactiva con el resultado de la consulta.
	 */

    @Override
    public Response getClient(String code) {
        
        if (ERROR_CODES.containsKey(code)) {
            return ERROR_CODES.get(code).get();
        }

        ClientDTO client = OK_CLIENTS.get(code);
        if (client != null) {
            return Response.ok(client).build();
        }

        return Response.status(NOT_FOUND).entity("Cliente no encontrado").build();
    }
}
