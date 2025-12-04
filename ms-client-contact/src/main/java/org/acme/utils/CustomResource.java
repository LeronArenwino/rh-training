package org.acme.utils;

import java.util.function.Function;
import org.acme.utils.response.CustomHeader;
import org.acme.utils.response.ResponseBody;

import jakarta.ws.rs.core.Response;

/**
 * Clase base abstracta para recursos REST personalizados.
 *
 * Proporciona métodos utilitarios para construir respuestas HTTP estandarizadas
 * tanto para casos de éxito como de error.
 * 
 * @author Angel Gonzalez
 * @since 2025-10-13
 * @version 1.0.0
 */
public abstract class CustomResource {

    /**
     * Construye una respuesta HTTP con un objeto, código de estado y mensaje
     * proporcionados.
     *
     * @param <T>     El tipo de objeto a incluir en el cuerpo de la respuesta.
     * @param object  El objeto a incluir en el cuerpo de la respuesta.
     * @param code    El código de estado HTTP de la respuesta.
     * @param message El mensaje de la respuesta.
     * @return Una instancia de {@link Response} con el cuerpo, código de estado y
     *         encabezado especificados.
     */
    private <T> Response buildResponse(T object, int code, String message) {
        var header = new CustomHeader();
        header.setResponseCode(code);
        header.setResponseMessage(message);
        var responseBody = new ResponseBody<T>();

        responseBody.setHeader(header);
        responseBody.setBody(object);

        return Response.status(code)
                .entity(responseBody)
                .build();
    }
    
    /**
     * Construye una respuesta HTTP de error con un código de estado y mensaje
     * proporcionados.
     *
     * @param code    El código de estado HTTP de la respuesta.
     * @param message El mensaje de la respuesta.
     * @return Una instancia de {@link Response} que representa una respuesta de
     *         error.
     */
    protected Response response(int code, String message) {
        return buildResponse(null, code, message);
    }

    /**
     * Proporciona una función que construye una respuesta HTTP de éxito de manera
     * reactiva.
     *
     * @param <T>     El tipo de objeto a incluir en el cuerpo de la respuesta.
     * @param code    El código de estado HTTP de la respuesta.
     * @param message El mensaje de la respuesta.
     * @return Una función que toma un objeto y devuelve una instancia de
     *         {@link Response}.
     */
    protected <T> Function<T, Response> reactiveSuccessResponse(int code, String message) {
        return object -> buildResponse(object, code, message);
    }
}
