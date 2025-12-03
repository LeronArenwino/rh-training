package org.acme.dto;

import java.util.List;

public record ClientDTO(
    String fullName,
    String document,
    String position,
    String email,
    String phoneNumber,
    String mobileNumber,
    List<AddressDTO> addresses
) {}