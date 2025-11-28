package co.com.training.contracts;

import java.util.List;

import co.com.training.model.Filial;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/v1/filial")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface FilialsContract {

    @POST
    public Filial createFilial(Filial filial);

    @GET
    @Path("/{id}")
    public Filial getFilialById(@PathParam("id") String id);

    @GET
    @Path("/all")
    public List<Filial> getFilials();
}
