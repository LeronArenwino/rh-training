package org.acme.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public class Programs {

    @NotEmpty(message = "El campo 'code' no debe estar vacío")
    @Pattern(regexp = "^[A-Z0-9]{3}$", message = "El campo code debe ser de 3 caracteres")
    public String code;

    public Boolean active;

    @NotEmpty(message = "El campo 'currencyCode' no debe estar vacío")
    @Pattern(regexp = "^[A-Z]{3}$", message = "El campo currencyCode debe ser de 3 caracteres")
    public String currencyCode;

    public String description;

    @NotEmpty(message = "El campo 'paymentType' no debe estar vacío")
    @Pattern(regexp = "^[A-Z]{3}$", message = "El campo paymentType debe ser de 3 caracteres")
    public String paymentType;

    @NotEmpty(message = "El campo 'transactionChannel' no debe estar vacío")
    @Pattern(regexp = "^[A-Z]{3}$", message = "El campo transactionChannel debe ser de 3 caracteres")
    public String transactionChannel;
}
