package org.acme.model;

import io.smallrye.mutiny.Uni;
import jakarta.persistence.Entity;

/**
 * Clase que representa la entidad Client para realizar la consulta a la BD.
 * Extiende BaseClientFields para reutilizar campos y métodos comunes.
 * 
 * @author Felipe Malaver
 * @since 2025-12-09
 * @version 1.0
 */
@Entity
public class Client extends BaseClientFields {
    // Todos los campos y métodos se heredan de BaseClientFields, eliminando duplicación

    public static Uni<Client> findByDocument(String document) {
        return find("document", document).firstResult();
    }

    public Client() {
    }

    public Client(String document, String documentType, String name, String phone, String email, String address, String creditCard) {
        super(document, documentType, name, phone, email, address, creditCard);
    }
    
    // Los getters y setters se heredan de BaseClientFields, eliminando duplicación
}
