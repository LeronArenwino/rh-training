package org.acme.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record AddressDTO(
    String city,
    String country,
    String postalCode
) {}