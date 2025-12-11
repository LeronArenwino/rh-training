package org.acme.model;

public record ClientDTO(
    String document,
    String documentType,
    String name,
    String phone,
    String email,
    String address,
    String creditCard
) {}