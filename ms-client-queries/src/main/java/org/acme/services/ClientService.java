package org.acme.services;

import java.util.Optional;
import org.acme.model.ClientCache;

import io.smallrye.mutiny.Uni;

public interface ClientService {

    Uni<Optional<ClientCache>> getClient(String document);

}
