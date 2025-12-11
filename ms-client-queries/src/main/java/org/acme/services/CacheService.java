package org.acme.services;

import org.acme.model.ClientCache;
import io.smallrye.mutiny.Uni;

public interface CacheService {

    Uni<ClientCache> getAsyncData(String id);
    
    Uni<ClientCache> putAsyncData(String id, ClientCache client);
}
