package org.acme.mapper;

import org.acme.model.Address;
import org.mapstruct.Mapper;
import org.acme.dto.AddressDTO;


@Mapper(componentModel = "cdi")
public interface AddressMapper {

    AddressDTO toDTO(Address entity);

}
