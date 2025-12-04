package co.com.training.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

/**
 * Entidad que representa un programa asociado a una filial.
 * 
 * <p>Un programa define las características de un programa de lealtad, descuentos
 * o promociones que una filial puede ofrecer. Esta entidad mantiene una relación
 * muchos a uno con {@link Filial}.
 * 
 * <p>Esta entidad extiende de {@link PanacheEntity} para proporcionar
 * funcionalidades de persistencia simplificadas.
 * 
 * @author Francisco Dueñas
 * @version 1.0.0
 * @see Filial
 */
@Entity
@Table(name = "programs")
@Schema(description = "Representa un programa asociado a una filial")
public class Programs extends PanacheEntity {

    /**
     * Código único del programa.
     * 
     * <p>Debe contener solo letras mayúsculas y números. No puede estar vacío.
     */
    @NotEmpty(message = "El campo 'code' no debe estar vacío")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "El campo code debe contener solo letras mayúsculas y números")
    @Schema(description = "Código único del programa", example = "PROG001", required = true)
    public String code;

    /**
     * Indica si el programa está activo.
     * 
     * <p>Cuando es true, el programa está disponible para uso.
     */
    @Schema(description = "Indica si el programa está activo", example = "true")
    public Boolean active;

    /**
     * Código de moneda según estándar ISO 4217.
     * 
     * <p>Debe ser exactamente 3 caracteres en mayúsculas (ej: DOP, USD, EUR).
     */
    @NotEmpty(message = "El campo 'currencyCode' no debe estar vacío")
    @Pattern(regexp = "^[A-Z]{3}$", message = "El campo currencyCode debe ser de 3 caracteres")
    @Schema(description = "Código de moneda ISO 4217", example = "DOP", required = true)
    public String currencyCode;

    /**
     * Descripción del programa.
     * 
     * <p>Descripción textual que explica el propósito y características del programa.
     */
    @Schema(description = "Descripción del programa", example = "Programa de Lealtad")
    public String description;

    /**
     * Tipo de pago aceptado por el programa.
     * 
     * <p>Debe contener solo letras mayúsculas y guiones bajos (ej: CREDIT_CARD, CASH).
     * No puede estar vacío.
     */
    @NotEmpty(message = "El campo 'paymentType' no debe estar vacío")
    @Pattern(regexp = "^[A-Z_]+$", message = "El campo paymentType debe contener solo letras mayúsculas y guiones bajos")
    @Schema(description = "Tipo de pago aceptado", example = "CREDIT_CARD", required = true)
    public String paymentType;

    /**
     * Canal de transacción del programa.
     * 
     * <p>Debe contener solo letras mayúsculas y guiones bajos (ej: ONLINE, BRANCH).
     * No puede estar vacío.
     */
    @NotEmpty(message = "El campo 'transactionChannel' no debe estar vacío")
    @Pattern(regexp = "^[A-Z_]+$", message = "El campo transactionChannel debe contener solo letras mayúsculas y guiones bajos")
    @Schema(description = "Canal de transacción", example = "ONLINE", required = true)
    public String transactionChannel;

    /**
     * Filial a la que pertenece este programa.
     * 
     * <p>Relación muchos a uno con la entidad Filial.
     */
    @ManyToOne
    @Schema(description = "Filial asociada al programa")
    public Filial filial;
}
