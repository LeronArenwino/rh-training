package org.acme.utils.response;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Clase que representa un encabezado personalizado para las respuestas HTTP.
 *
 * Esta clase contiene información sobre el código de respuesta y el mensaje de respuesta
 * que se puede utilizar para proporcionar detalles adicionales en las respuestas HTTP.
 * 
 * @author Angel Gonzalez
 * @since 2025-10-13
 * @version 1.0.0
 */
@RegisterForReflection
public class CustomHeader {

    private int responseCode;
    private String responseMessage;

    /**
     * Obtiene el código de respuesta HTTP.
     *
     * @return El código de respuesta HTTP como un valor entero.
     */
    public int getResponseCode() {
        return responseCode;
    }

    /**
     * Establece el código de respuesta HTTP.
     *
     * @param responseCode El código de respuesta HTTP a establecer.
     */
    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    /**
     * Obtiene el mensaje de respuesta HTTP.
     *
     * @return El mensaje de respuesta HTTP como una cadena de texto.
     */
    public String getResponseMessage() {
        return responseMessage;
    }

    /**
     * Establece el mensaje de respuesta HTTP.
     *
     * @param responseMessage El mensaje de respuesta HTTP a establecer.
     */
    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }
}

