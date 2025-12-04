package co.com.training.resources;

import java.util.List;

import org.jboss.logging.Logger;

import co.com.training.contracts.FilialsContract;
import co.com.training.model.Filial;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

/**
 * Implementación del recurso REST para la gestión de filiales.
 * 
 * <p>Esta clase proporciona la implementación concreta de los endpoints definidos
 * en {@link FilialsContract}, manejando la lógica de negocio y la persistencia
 * de datos mediante Hibernate Panache.
 * 
 * <p>Los métodos de esta clase están anotados con {@code @Transactional} para
 * garantizar la integridad de las transacciones de base de datos.
 * 
 * @author Francisco Dueñas
 * @version 1.0.0
 * @see FilialsContract
 */
@ApplicationScoped
public class FilialResource implements FilialsContract {

    private final Logger logger = Logger.getLogger(FilialResource.class);

    /**
     * {@inheritDoc}
     * 
     * <p>Implementa la creación de una nueva filial. Si la filial no tiene ID,
     * se crea una nueva entidad. Si tiene ID, se actualiza la entidad existente.
     */
    @Override
    @Transactional
    public Filial createFilial(Filial filial) {
        logger.info("Recibiendo petición para crear filial: " + filial.name);
        
        if (filial.id == null) {
            filial.persist();
            logger.info("Filial creada con ID: " + filial.id);
        } else {
            Filial.getEntityManager().merge(filial);
            logger.info("Filial actualizada con ID: " + filial.id);
        }
        return filial;
    }

    /**
     * {@inheritDoc}
     * 
     * <p>Implementa la consulta de todas las filiales. Retorna una lista vacía
     * si no hay filiales registradas.
     */
    @Override
    public List<Filial> getFilials() {
        List<Filial> filials = Filial.findAll().list();
        logger.info("Filiales encontradas: " + filials.size());
        return filials;
    }

    /**
     * {@inheritDoc}
     * 
     * <p>Implementa la consulta de una filial por ID. Valida que el ID sea
     * un número válido y que la filial exista en la base de datos.
     * 
     * @throws WebApplicationException Si el ID es inválido (400) o la filial
     *         no existe (404).
     */
    @Override
    public Filial getFilialById(String id) {
        logger.info("Filial ID: " + id);
        try {
            Long filialId = Long.parseLong(id);
            Filial filial = Filial.findById(filialId);
            if (filial == null) {
                throw new WebApplicationException("Filial not found", Response.Status.NOT_FOUND);
            }
            return filial;
        } catch (NumberFormatException e) {
            throw new WebApplicationException("Invalid filial ID format", Response.Status.BAD_REQUEST);
        }
    }

}
