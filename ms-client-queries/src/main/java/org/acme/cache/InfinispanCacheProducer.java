package org.acme.cache;

import io.quarkus.arc.profile.IfBuildProfile;
import io.quarkus.infinispan.client.Remote;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Produces;

import static org.acme.utils.constants.Constants.CACHE_REMOTE_NAME;

import org.acme.model.ClientCache;
import org.infinispan.client.hotrod.RemoteCache;

@ApplicationScoped
@IfBuildProfile("prod")
public class InfinispanCacheProducer {

    @Produces
    @ApplicationScoped
    @Remote(CACHE_REMOTE_NAME)
    public RemoteCache<String, ClientCache> cache() {
        // conexión real a Infinispan
        throw new UnsupportedOperationException("Prod only");
    }
}
