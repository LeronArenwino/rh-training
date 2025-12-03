package org.acme.resource;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/external-client")
@RegisterRestClient(configKey = "base")
@Produces(MediaType.APPLICATION_JSON)
public interface ExternalClientMockResource {

    @GET
    @Path("/{code}")
    public Response getClient(@PathParam("code") String code);
}
