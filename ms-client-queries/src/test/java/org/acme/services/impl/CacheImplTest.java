package org.acme.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.acme.model.ClientCache;
import org.acme.services.CacheService;
import org.infinispan.client.hotrod.RemoteCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;

/**
 * Clase de pruebas unitarias para CacheImpl.
 * 
 * @author Felipe Malaver
 * @since 2025-12-09
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
class CacheImplTest {

    @Mock
    private RemoteCache<String, ClientCache> remoteCache;

    private CacheService cacheService;

    @BeforeEach
    void setUp() {
        cacheService = new CacheImpl(remoteCache);
    }

    @Test
    @DisplayName("Debería obtener datos de la caché de forma asíncrona cuando el cliente existe")
    void testGetAsyncData_WhenClientExists_ShouldReturnClientCache() {
        // Arrange
        String clientId = "12345";
        ClientCache expectedClient = createTestClientCache("12345", "CC", "John Doe", 
                "1234567890", "john@example.com", "123 Main St", "1234-5678-9012-3456");

        CompletableFuture<ClientCache> future = CompletableFuture.completedFuture(expectedClient);
        when(remoteCache.getAsync(clientId)).thenReturn(future);

        // Act
        Uni<ClientCache> result = cacheService.getAsyncData(clientId);

        // Assert
        UniAssertSubscriber<ClientCache> subscriber = result
                .subscribe().withSubscriber(UniAssertSubscriber.create());
        
        subscriber.assertCompleted().assertItem(expectedClient);
        verify(remoteCache).getAsync(clientId);
    }

    @Test
    @DisplayName("Debería retornar null cuando el cliente no existe en la caché")
    void testGetAsyncData_WhenClientDoesNotExist_ShouldReturnNull() {
        // Arrange
        String clientId = "99999";
        CompletableFuture<ClientCache> future = CompletableFuture.completedFuture(null);
        when(remoteCache.getAsync(clientId)).thenReturn(future);

        // Act
        Uni<ClientCache> result = cacheService.getAsyncData(clientId);

        // Assert
        UniAssertSubscriber<ClientCache> subscriber = result
                .subscribe().withSubscriber(UniAssertSubscriber.create());
        
        subscriber.assertCompleted().assertItem(null);
        verify(remoteCache).getAsync(clientId);
    }

    @Test
    @DisplayName("Debería almacenar datos en la caché de forma asíncrona y retornar el cliente")
    void testPutAsyncData_ShouldStoreClientAndReturnIt() {
        // Arrange
        String clientId = "12345";
        ClientCache clientToStore = createTestClientCache("12345", "CC", "John Doe", 
                "1234567890", "john@example.com", "123 Main St", "1234-5678-9012-3456");

        CompletableFuture<Void> putFuture = CompletableFuture.completedFuture(null);
        doReturn(putFuture).when(remoteCache).putAsync(anyString(), any(ClientCache.class), anyLong(), 
                any(TimeUnit.class), anyLong(), any(TimeUnit.class));

        // Act
        Uni<ClientCache> result = cacheService.putAsyncData(clientId, clientToStore);

        // Assert
        UniAssertSubscriber<ClientCache> subscriber = result
                .subscribe().withSubscriber(UniAssertSubscriber.create());
        
        subscriber.assertCompleted().assertItem(clientToStore);
        verify(remoteCache).putAsync(clientId, clientToStore, 0L, 
                TimeUnit.MILLISECONDS, 0L, TimeUnit.MILLISECONDS);
    }

    @Test
    @DisplayName("Debería almacenar datos con diferentes valores de cliente")
    void testPutAsyncData_WithDifferentClientValues_ShouldStoreAndReturn() {
        // Arrange
        String clientId = "67890";
        ClientCache clientToStore = createTestClientCache("67890", "TI", "Jane Smith", 
                "0987654321", "jane@example.com", "456 Oak Ave", "9876-5432-1098-7654");

        CompletableFuture<Void> putFuture = CompletableFuture.completedFuture(null);
        doReturn(putFuture).when(remoteCache).putAsync(anyString(), any(ClientCache.class), anyLong(), 
                any(TimeUnit.class), anyLong(), any(TimeUnit.class));

        // Act
        Uni<ClientCache> result = cacheService.putAsyncData(clientId, clientToStore);

        // Assert
        UniAssertSubscriber<ClientCache> subscriber = result
                .subscribe().withSubscriber(UniAssertSubscriber.create());
        
        subscriber.assertCompleted().assertItem(clientToStore);
        assertThat(subscriber.getItem())
                .isNotNull()
                .extracting("document", "name")
                .containsExactly("67890", "Jane Smith");
    }

    @Test
    @DisplayName("Debería manejar errores en getAsyncData cuando la caché falla")
    void testGetAsyncData_WhenCacheFails_ShouldPropagateError() {
        // Arrange
        String clientId = "12345";
        RuntimeException exception = new RuntimeException("Cache error");
        CompletableFuture<ClientCache> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(exception);
        
        when(remoteCache.getAsync(clientId)).thenReturn(failedFuture);

        // Act
        Uni<ClientCache> result = cacheService.getAsyncData(clientId);

        // Assert
        UniAssertSubscriber<ClientCache> subscriber = result
                .subscribe().withSubscriber(UniAssertSubscriber.create());
        
        subscriber.assertFailedWith(RuntimeException.class, "Cache error");
        verify(remoteCache).getAsync(clientId);
    }

    @Test
    @DisplayName("Debería manejar errores en putAsyncData cuando la caché falla")
    void testPutAsyncData_WhenCacheFails_ShouldPropagateError() {
        // Arrange
        String clientId = "12345";
        ClientCache clientToStore = createTestClientCache("12345", "CC", "John Doe", 
                "1234567890", "john@example.com", "123 Main St", "1234-5678-9012-3456");
        
        RuntimeException exception = new RuntimeException("Cache write error");
        CompletableFuture<Void> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(exception);
        
        doReturn(failedFuture).when(remoteCache).putAsync(anyString(), any(ClientCache.class), anyLong(), 
                any(TimeUnit.class), anyLong(), any(TimeUnit.class));

        // Act
        Uni<ClientCache> result = cacheService.putAsyncData(clientId, clientToStore);

        // Assert
        UniAssertSubscriber<ClientCache> subscriber = result
                .subscribe().withSubscriber(UniAssertSubscriber.create());
        
        subscriber.assertFailedWith(RuntimeException.class, "Cache write error");
        verify(remoteCache).putAsync(clientId, clientToStore, 0L, 
                TimeUnit.MILLISECONDS, 0L, TimeUnit.MILLISECONDS);
    }

    @Test
    @DisplayName("Debería crear la instancia correctamente con el constructor")
    void testConstructor_ShouldCreateInstanceWithRemoteCache() {
        // Arrange & Act
        CacheImpl cacheImpl = new CacheImpl(remoteCache);

        // Assert
        assertThat(cacheImpl)
                .isNotNull()
                .isInstanceOf(CacheService.class);
    }

    /**
     * Método auxiliar para crear instancias de ClientCache para pruebas.
     */
    private ClientCache createTestClientCache(String document, String documentType, 
            String name, String phone, String email, String address, String creditCard) {
        ClientCache client = new ClientCache();
        client.setDocument(document);
        client.setDocumentType(documentType);
        client.setName(name);
        client.setPhone(phone);
        client.setEmail(email);
        client.setAddress(address);
        client.setCreditCard(creditCard);
        return client;
    }
}

