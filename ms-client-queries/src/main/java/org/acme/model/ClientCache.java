package org.acme.model;

import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;


public class ClientCache {
    
    @ProtoField(number = 1)
    public String document;

    @ProtoField(number = 2)
    public String documentType;
    
    @ProtoField(number = 3)
    public String name;
    
    @ProtoField(number = 4)
    public String phone;

    @ProtoField(number = 5)
    public String email;
    
    @ProtoField(number = 6)
    public String address;
    
    @ProtoField(number = 7)
    public String creditCard;

    public ClientCache() {
    }

    /*
     * Constructor para la serialización Protobuf.
     * @param document El documento del cliente.
     * @param documentType El tipo de documento del cliente.
     * @param name El nombre del cliente.
     * @param phone El teléfono del cliente.
     * @param email El correo electrónico del cliente.
     * @param address La dirección del cliente.
     * @param creditCard La tarjeta de crédito del cliente. 
     * 
     * @ProtoFactory Indica a Protobuf que este constructor debe usarse 
     * para crear instancias durante la deserialización.
     */
    @ProtoFactory
    public ClientCache(String document, String documentType, String name, String phone, String email, String address, String creditCard) {
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
