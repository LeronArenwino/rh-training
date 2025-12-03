package org.acme.mapper;

import org.acme.model.Client;


import org.acme.dto.ClientDTO;
import org.mapstruct.Mapper;


@Mapper(componentModel = "cdi" ,uses = {AddressMapper.class})
public interface ClientMapper {

    ClientDTO toDTO(Client entity);

    Client toEntity(ClientDTO dto);
}
