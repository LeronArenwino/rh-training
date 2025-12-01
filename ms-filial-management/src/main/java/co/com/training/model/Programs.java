package co.com.training.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "programs")
public class Programs extends PanacheEntity {

    @NotEmpty(message = "El campo 'code' no debe estar vacío")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "El campo code debe contener solo letras mayúsculas y números")
    public String code;

    public Boolean active;

    @NotEmpty(message = "El campo 'currencyCode' no debe estar vacío")
    @Pattern(regexp = "^[A-Z]{3}$", message = "El campo currencyCode debe ser de 3 caracteres")
    public String currencyCode;

    public String description;

    @NotEmpty(message = "El campo 'paymentType' no debe estar vacío")
    @Pattern(regexp = "^[A-Z_]+$", message = "El campo paymentType debe contener solo letras mayúsculas y guiones bajos")
    public String paymentType;

    @NotEmpty(message = "El campo 'transactionChannel' no debe estar vacío")
    @Pattern(regexp = "^[A-Z_]+$", message = "El campo transactionChannel debe contener solo letras mayúsculas y guiones bajos")
    public String transactionChannel;

    @ManyToOne
    public Filial filial;
}
