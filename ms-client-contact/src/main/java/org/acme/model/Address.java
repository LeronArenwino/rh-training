package org.acme.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Address extends PanacheEntity {

    public String city;
    public String country;
    public String postalCode;

    @ManyToOne
    @JoinColumn(name = "client_id")
    public Client client;

    public Address() {
    }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
}
