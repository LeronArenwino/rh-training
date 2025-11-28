package org.acme.model;

import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "filiales")
public class Filial extends PanacheEntity {

    @NotEmpty(message = "El campo 'name' no debe estar vacío")
    @Pattern(regexp = "^[A-Za-z0-9\\s]+$", message = "El campo name debe contener solo letras y números")
    public String name;

    @NotEmpty(message = "El campo 'type' no debe estar vacío")
    @Pattern(regexp = "^[A-Za-z0-9\\s]+$", message = "El campo type debe contener solo letras y números")
    public String type;

    @NotEmpty(message = "El campo 'countryCode' no debe estar vacío")
    @Pattern(regexp = "^[A-Z]{2}$", message = "El campo countryCode debe ser de 2 caracteres en mayúsculas")
    public String countryCode;

    @NotEmpty(message = "El campo 'socialRegister' no debe estar vacío")
    @Pattern(regexp = "^[A-Za-z0-9\\s]+$", message = "El campo socialRegister debe contener solo letras y números")
    public String socialRegister;

    @NotEmpty(message = "El campo 'rnc' no debe estar vacío")
    @Pattern(regexp = "^[A-Za-z0-9\\s]+$", message = "El campo rnc debe contener solo letras y números")
    public String rnc;

    @NotNull(message = "El campo 'status' no debe ser nulo")
    public Boolean status;

    @NotEmpty(message = "El campo 'programs' no debe estar vacío")
     @Valid
    @OneToMany(mappedBy = "filial", cascade = CascadeType.ALL)
    public List<Programs> programs;
}
