package co.com.training.resources;

import java.util.List;

import org.jboss.logging.Logger;

import co.com.training.contracts.FilialsContract;
import co.com.training.model.Filial;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class FilialResource implements FilialsContract {

    private final Logger logger = Logger.getLogger(FilialResource.class);

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

    public List<Filial> getFilials() {
        List<Filial> filials = Filial.findAll().list();
        logger.info("Filiales encontradas: " + filials.size());
        return filials;
    }

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
