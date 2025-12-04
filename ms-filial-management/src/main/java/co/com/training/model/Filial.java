package co.com.training.model;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * Entidad que representa una filial en el sistema.
 * 
 * <p>Una filial es una sucursal o punto de venta que puede tener múltiples
 * programas asociados. Esta entidad extiende de {@link PanacheEntity} para
 * proporcionar funcionalidades de persistencia simplificadas.
 * 
 * <p>La relación con {@link Programs} es de uno a muchos, donde una filial
 * puede tener múltiples programas, y cada programa pertenece a una única filial.
 * 
 * @author Francisco Dueñas
 * @version 1.0.0
 * @see Programs
 */
@Entity
@Table(name = "filiales")
@Schema(description = "Representa una filial o sucursal con sus programas asociados")
public class Filial extends PanacheEntity {

    /**
     * Nombre de la filial.
     *  
     * <p>Debe contener solo letras, números y espacios. No puede estar vacío.
     */
    @NotEmpty(message = "El campo 'name' no debe estar vacío")
    @Pattern(regexp = "^[A-Za-z0-9\\s]+$", message = "El campo name debe contener solo letras y números")
    @Schema(description = "Nombre de la filial", example = "Filial Principal", required = true)
    public String name;

    /**
     * Tipo de filial (ej: SUCURSAL, CENTRAL, etc.).
     * 
     * <p>Debe contener solo letras, números y espacios. No puede estar vacío.
     */
    @NotEmpty(message = "El campo 'type' no debe estar vacío")
    @Pattern(regexp = "^[A-Za-z0-9\\s]+$", message = "El campo type debe contener solo letras y números")
    @Schema(description = "Tipo de filial", example = "SUCURSAL", required = true)
    public String type;

    /**
     * Código de país según estándar ISO 3166-1 alpha-2.
     * 
     * <p>Debe ser exactamente 2 caracteres en mayúsculas (ej: DO, US, MX).
     */
    @NotEmpty(message = "El campo 'countryCode' no debe estar vacío")
    @Pattern(regexp = "^[A-Z]{2}$", message = "El campo countryCode debe ser de 2 caracteres en mayúsculas")
    @Schema(description = "Código de país ISO 3166-1 alpha-2", example = "DO", required = true)
    public String countryCode;

    /**
     * Registro social de la filial.
     * 
     * <p>Debe contener solo letras, números y espacios. No puede estar vacío.
     */
    @NotEmpty(message = "El campo 'socialRegister' no debe estar vacío")
    @Pattern(regexp = "^[A-Za-z0-9\\s]+$", message = "El campo socialRegister debe contener solo letras y números")
    @Schema(description = "Registro social de la filial", example = "123456789", required = true)
    public String socialRegister;

    /**
     * RNC (Registro Nacional del Contribuyente) de la filial.
     * 
     * <p>Debe contener solo letras, números y espacios. No puede estar vacío.
     */
    @NotEmpty(message = "El campo 'rnc' no debe estar vacío")
    @Pattern(regexp = "^[A-Za-z0-9\\s]+$", message = "El campo rnc debe contener solo letras y números")
    @Schema(description = "RNC de la filial", example = "1311234567", required = true)
    public String rnc;

    /**
     * Estado activo/inactivo de la filial.
     * 
     * <p>Indica si la filial está activa en el sistema. No puede ser nulo.
     */
    @NotNull(message = "El campo 'status' no debe ser nulo")
    @Schema(description = "Estado activo/inactivo de la filial", example = "true", required = true)
    public Boolean status;

    /**
     * Lista de programas asociados a la filial.
     * 
     * <p>Debe contener al menos un programa. Los programas se crean o actualizan
     * en cascada cuando se persiste la filial.
     */
    @NotEmpty(message = "El campo 'programs' no debe estar vacío")
    @Valid
    @OneToMany(mappedBy = "filial", cascade = CascadeType.ALL)
    @Schema(description = "Lista de programas asociados a la filial", required = true)
    public List<Programs> programs;
}
