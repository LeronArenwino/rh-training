package org.acme.model;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

/**
 * Clase que representa la entidad Client para realizar la consulta a la BD.
 * 
 * @author Felipe Malaver
 * @since 2025-12-09
 * @version 1.0
 */

@Entity
public class Client extends PanacheEntity {
    
    @Column(unique = true)
    public String document;

    @Column(name = "document_type")
    public String documentType;

    public String name;
    public String phone;
    @Column(unique = true)
    public String email;
    public String address;

    @Column(name = "credit_card")
    public String creditCard;

    public static Uni<Client> findByDocument(String document) {
        return find("document", document).firstResult();
    }

    public Client() {
    }

    public Client(String document, String documentType, String name, String phone, String email, String address, String creditCard) {
        this.document = document;
        this.documentType = documentType;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.creditCard = creditCard;
    }
}
