package org.acme.resource.impl;

import org.acme.resource.ClientResource;
import org.acme.services.ClientService;
import org.acme.utils.CustomResource;

import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

import static org.acme.utils.constants.Constants.OK;
import static org.acme.utils.constants.Constants.NOT_FOUND;

/**
 * Clase que implementa los endpoints REST para la gestión de clientes.
 * 
 * @author Felipe Malaver
 * @since 2025-12-09
 * @version 1.0
 */
public class ClientImplement extends CustomResource implements ClientResource {

    private final ClientService clientService;

    @Inject
	public ClientImplement(ClientService clientService) {
		this.clientService = clientService;
	}

	/*
	 * Endpoint para obtener un cliente por su documento.
	 * @return Uni<Response> Respuesta HTTP con el cliente o error si no se encuentra.
	 * @param document El documento del cliente a buscar.
	 * 
	 */
    @Override
	public Uni<Response> getClientByDocument(String document) {
	return clientService.getClient(document)
			.onItem().transformToUni(optional -> optional
					.map(value -> Uni.createFrom()
							.item(reactiveSuccessResponse(OK, "Cliente consultado exitosamente").apply(value)))
					.orElseGet(() -> Uni.createFrom()
							.item(response(NOT_FOUND,
									"El cliente no fue encontrado o no existe"))));
    }

}
