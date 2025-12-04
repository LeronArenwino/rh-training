package co.com.training.contracts;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import co.com.training.model.Filial;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Contrato que define los endpoints REST para la gestión de filiales.
 * 
 * <p>Esta interfaz define los métodos HTTP disponibles para realizar operaciones
 * CRUD sobre las filiales, incluyendo la creación, consulta individual y
 * consulta de todas las filiales.
 * 
 * @author Francisco Dueñas
 * @version 1.0.0
 */
@Path("/api/v1/filial")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Filiales", description = "Operaciones para la gestión de filiales")
public interface FilialsContract {

    /**
     * Crea una nueva filial en el sistema.
     * 
     * <p>Este endpoint permite crear una nueva filial con sus programas asociados.
     * La filial y sus programas serán persistidos en la base de datos.
     * 
     * @param filial La filial a crear. Debe contener todos los campos requeridos
     *               y al menos un programa asociado.
     * @return La filial creada con su ID asignado.
     * @throws jakarta.validation.ConstraintViolationException Si los datos de la filial
     *         no cumplen con las validaciones requeridas.
     */
    @POST
    @Operation(
        summary = "Crear una nueva filial",
        description = "Crea una nueva filial en el sistema con sus programas asociados"
    )
    @APIResponses(value = {
        @APIResponse(
            responseCode = "200",
            description = "Filial creada exitosamente",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = Filial.class)
            )
        ),
        @APIResponse(
            responseCode = "400",
            description = "Datos de la filial inválidos o no cumplen con las validaciones"
        )
    })
    public Filial createFilial(@Valid Filial filial);

    /**
     * Obtiene una filial por su identificador único.
     * 
     * <p>Este endpoint permite consultar una filial específica mediante su ID.
     * El ID debe ser un número válido.
     * 
     * @param id El identificador único de la filial (debe ser un número).
     * @return La filial encontrada.
     * @throws jakarta.ws.rs.WebApplicationException Con código 404 si la filial no existe,
     *         o código 400 si el formato del ID es inválido.
     */
    @GET
    @Path("/{id}")
    @Operation(
        summary = "Obtener filial por ID",
        description = "Recupera una filial específica mediante su identificador único"
    )
    @APIResponses(value = {
        @APIResponse(
            responseCode = "200",
            description = "Filial encontrada",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = Filial.class)
            )
        ),
        @APIResponse(
            responseCode = "400",
            description = "Formato de ID inválido"
        ),
        @APIResponse(
            responseCode = "404",
            description = "Filial no encontrada"
        )
    })
    public Filial getFilialById(
        @Parameter(description = "Identificador único de la filial", required = true)
        @PathParam("id") String id
    );

    /**
     * Obtiene todas las filiales registradas en el sistema.
     * 
     * <p>Este endpoint retorna una lista con todas las filiales disponibles,
     * incluyendo sus programas asociados.
     * 
     * @return Una lista de todas las filiales. Si no hay filiales registradas,
     *         retorna una lista vacía.
     */
    @GET
    @Path("/all")
    @Operation(
        summary = "Obtener todas las filiales",
        description = "Recupera todas las filiales registradas en el sistema"
    )
    @APIResponse(
        responseCode = "200",
        description = "Lista de filiales",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = Filial.class)
        )
    )
    public List<Filial> getFilials();
}
