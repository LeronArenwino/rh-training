package org.acme.model;

import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

/**
 * Clase que representa los datos del cliente para almacenamiento en caché.
 * Extiende BaseClientFields para reutilizar campos y métodos comunes.
 * Los campos están en la clase base y los getters se sobrescriben con @ProtoField
 * para la serialización de Infinispan.
 * 
 * @author Felipe Malaver
 * @since 2025-12-09
 * @version 1.0
 */
public class ClientCache extends BaseClientFields {
    
    public ClientCache() {
        super();
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
        super(document, documentType, name, phone, email, address, creditCard);
    }

    // Getters sobrescritos con @ProtoField para serialización Protobuf
    // Los campos se heredan de BaseClientFields
    @ProtoField(number = 1)
    @Override
    public String getDocument() {
        return super.getDocument();
    }

    @ProtoField(number = 2)
    @Override
    public String getDocumentType() {
        return super.getDocumentType();
    }
    
    @ProtoField(number = 3)
    @Override
    public String getName() {
        return super.getName();
    }
    
    @ProtoField(number = 4)
    @Override
    public String getPhone() {
        return super.getPhone();
    }

    @ProtoField(number = 5)
    @Override
    public String getEmail() {
        return super.getEmail();
    }
    
    @ProtoField(number = 6)
    @Override
    public String getAddress() {
        return super.getAddress();
    }
    
    @ProtoField(number = 7)
    @Override
    public String getCreditCard() {
        return super.getCreditCard();
    }
}
