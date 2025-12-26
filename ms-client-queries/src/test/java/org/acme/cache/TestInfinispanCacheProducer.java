package org.acme.cache;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

import static org.acme.utils.constants.Constants.CACHE_REMOTE_NAME;

import org.acme.model.ClientCache;
import org.mockito.Mockito;

import io.quarkus.infinispan.client.Remote;
import io.quarkus.arc.profile.IfBuildProfile;

import org.infinispan.client.hotrod.RemoteCache;


@ApplicationScoped
@IfBuildProfile("test")
public class TestInfinispanCacheProducer {

    @Produces
    @Remote(CACHE_REMOTE_NAME)
    public RemoteCache<String, ClientCache> cache() {
        return Mockito.mock(RemoteCache.class);
    }
}
