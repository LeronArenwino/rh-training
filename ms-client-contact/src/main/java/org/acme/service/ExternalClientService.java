package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.acme.resource.ExternalClientMockResource;

import java.util.Optional;
import java.util.function.Supplier;

@ApplicationScoped
public class ExternalClientService {

    @Inject
    ExternalClientMockResource externalClientMockResource;

    private final Supplier<Response> codeMissing =
            () -> Response.status(Response.Status.BAD_REQUEST)
                          .entity("Código es requerido")
                          .build();

    /**
     * Consume el servicio simulado y retorna el Response de manera funcional.
     */
    public Response fetchClient(String code) {
        // Validación funcional del código
        String validatedCode = Optional.ofNullable(code)
                                      .filter(c -> !c.isBlank())
                                      .orElseThrow(() -> new WebApplicationException(codeMissing.get()));

        // Intentamos obtener el cliente del mock y usamos Optional para manejar null
        return Optional.ofNullable(callMock(validatedCode))
                       .orElseThrow(() -> new WebApplicationException(
                               Response.status(Response.Status.NOT_FOUND)
                                       .entity(null)
                                       .build()
                       ));
    }

    /**
     * Llama al mock y captura WebApplicationException
     */
    private Response callMock(String code) {
        return Optional.ofNullable(externalClientMockResource)
                       .map(mock -> {
                           try {
                               return mock.getClient(code);
                           } catch (WebApplicationException ex) {
                               // Propaga la respuesta del mock
                               throw ex;
                           }
                       })
                       .orElseThrow(() -> new WebApplicationException(
                               Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                       .entity("Servicio externo no disponible")
                                       .build()
                       ));
    }
}