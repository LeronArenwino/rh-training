package org.acme.utils.response;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Clase genérica que representa el cuerpo de una respuesta HTTP.
 *
 * Esta clase encapsula el encabezado de la respuesta junto con el cuerpo de la
 * respuesta,
 * que puede ser de cualquier tipo especificado mediante un parámetro genérico.
 * 
 * @param <T> El tipo del cuerpo de la respuesta.
 * 
 * @author Angel Gonzalez
 * @since 2025-10-13
 * @version 1.0.0
 */
@RegisterForReflection
public class ResponseBody<T> {

    private CustomHeader header;
    private T body;

    /**
     * Obtiene el encabezado personalizado de la respuesta.
     *
     * @return Una instancia de {@link CustomHeader} que representa el encabezado de
     *         la respuesta.
     */
    public CustomHeader getHeader() {
        return header;
    }

    /**
     * Establece el encabezado personalizado de la respuesta.
     *
     * @param header Una instancia de {@link CustomHeader} que representa el
     *               encabezado de la respuesta.
     */
    public void setHeader(CustomHeader header) {
        this.header = header;
    }

    /**
     * Obtiene el cuerpo de la respuesta.
     *
     * @return El cuerpo de la respuesta de tipo `T`.
     */
    public T getBody() {
        return body;
    }

    /**
     * Establece el cuerpo de la respuesta.
     *
     * @param body El cuerpo de la respuesta de tipo `T`.
     */
    public void setBody(T body) {
        this.body = body;
    }
}
