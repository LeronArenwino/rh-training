package org.acme.model;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.Column;

/**
 * Clase base abstracta que contiene los campos y métodos comunes
 * para las clases Client y ClientCache, eliminando duplicación de código.
 * Extiende PanacheEntity para que Client pueda heredar de esta clase.
 * 
 * @author Felipe Malaver
 * @since 2025-12-09
 * @version 1.0
 */
public abstract class BaseClientFields extends PanacheEntity {
    
    @Column(unique = true)
    protected String document;

    @Column(name = "document_type")
    protected String documentType;

    protected String name;
    protected String phone;
    @Column(unique = true)
    protected String email;
    protected String address;

    @Column(name = "credit_card")
    protected String creditCard;

    protected BaseClientFields() {
    }

    protected BaseClientFields(String document, String documentType, String name, 
            String phone, String email, String address, String creditCard) {
        this.document = document;
        this.documentType = documentType;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.creditCard = creditCard;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(String creditCard) {
        this.creditCard = creditCard;
    }
}

