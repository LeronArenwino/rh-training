package org.acme.utils.constants;

import jakarta.ws.rs.core.Response;

public class Constants {

    /* Codigos Http */
    public static final int CREATED = Response.Status.CREATED.getStatusCode();
    public static final int BAD_REQUEST = Response.Status.BAD_REQUEST.getStatusCode();
    public static final int CONFLICT = Response.Status.CONFLICT.getStatusCode();
    public static final int OK = Response.Status.OK.getStatusCode();
    public static final int NOT_FOUND = Response.Status.NOT_FOUND.getStatusCode();
    public static final int NO_CONTENT = Response.Status.NO_CONTENT.getStatusCode();
    public static final int INTERNAL_SERVER_ERROR = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();

    public static final String CACHE_REMOTE_NAME = "CLIENT-LIST";

    private Constants() {
    }

}
