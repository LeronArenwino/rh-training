package org.acme.mapper;

import org.acme.model.Client;
import org.acme.model.ClientDTO;
import org.mapstruct.Mapper;

/**
 * Mapper para convertir la entidad {@link Client } a {@link ClientDTO}.
 * 
 * @author Felipe Malaver
 * @since 2025-12-09
 * @version 1.0
 */

@Mapper(componentModel = "cdi")
public interface ClientMapper {

    ClientDTO toDTO(Client entity);

}
