package org.acme.resources;

import java.util.List;

import org.acme.contracts.FilialsContract;
import org.acme.model.Filial;
import org.jboss.logging.Logger;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class FilialResource implements FilialsContract {

    private final Logger logger = Logger.getLogger(FilialResource.class);

    @Transactional
    public Filial createFilial(Filial filial) {
        
        if (filial.id == null) {
            filial.persist();
        } else {
            Filial.getEntityManager().merge(filial);
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
