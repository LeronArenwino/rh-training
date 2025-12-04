package org.acme.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.Produces;

@Path("/client")
@Produces(MediaType.APPLICATION_JSON)
public interface ClientResource {
    
    @GET
    @Path("/{code}")
    public Response getClient(@PathParam("code") String code, @HeaderParam("channel") String channel);
}
