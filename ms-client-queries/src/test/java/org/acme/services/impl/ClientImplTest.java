package org.acme.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.acme.model.Client;
import org.acme.model.ClientCache;
import org.acme.services.CacheService;
import org.acme.services.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

/**
 * Clase de pruebas unitarias para ClientImpl.
 * 
 * @author Felipe Malaver
 * @since 2025-12-09
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
class ClientImplTest {

    @Mock
    private CacheService cacheService;

    @Mock
    private Context context;

    private ClientService clientService;

    @BeforeEach
    void setUp() {
        clientService = new ClientImpl(cacheService);
    }

    @Test
    @DisplayName("Debería retornar el cliente cuando se encuentra en la caché")
    void testGetClient_WhenFoundInCache_ShouldReturnClient() {
        // Arrange
        String document = "12345";
        ClientCache cachedClient = createTestClientCache("12345", "CC", "John Doe", 
                "1234567890", "john@example.com", "123 Main St", "1234-5678-9012-3456");

        try (MockedStatic<Vertx> vertxMock = mockStatic(Vertx.class)) {
            vertxMock.when(Vertx::currentContext).thenReturn(context);
            doAnswer(invocation -> {
                Handler<Void> handler = invocation.getArgument(0);
                handler.handle(null);
                return null;
            }).when(context).runOnContext(any());
            
            CompletableFuture<ClientCache> future = CompletableFuture.completedFuture(cachedClient);
            when(cacheService.getAsyncData(document)).thenReturn(Uni.createFrom().completionStage(future));

            // Act
            Uni<Optional<ClientCache>> result = clientService.getClient(document);

            // Assert
            UniAssertSubscriber<Optional<ClientCache>> subscriber = result
                    .subscribe().withSubscriber(UniAssertSubscriber.create());
            
            subscriber.assertCompleted();
            assertThat(subscriber.getItem())
                    .isPresent()
                    .get()
                    .isEqualTo(cachedClient);
            verify(cacheService).getAsyncData(document);
        }
    }

    @Test
    @DisplayName("Debería consultar BD y almacenar en caché cuando no se encuentra en caché")
    void testGetClient_WhenNotInCache_ShouldFetchFromDbAndCache() {
        // Arrange
        String document = "12345";
        Client dbClient = createTestClient("12345", "CC", "John Doe", 
                "1234567890", "john@example.com", "123 Main St", "1234-5678-9012-3456");
        ClientCache expectedCache = createTestClientCache("12345", "CC", "John Doe", 
                "1234567890", "john@example.com", "123 Main St", "1234-5678-9012-3456");

        try (MockedStatic<Vertx> vertxMock = mockStatic(Vertx.class);
             MockedStatic<Client> clientMock = mockStatic(Client.class)) {
            
            vertxMock.when(Vertx::currentContext).thenReturn(context);
            doAnswer(invocation -> {
                Handler<Void> handler = invocation.getArgument(0);
                handler.handle(null);
                return null;
            }).when(context).runOnContext(any());

            // Mock cache returns null
            CompletableFuture<ClientCache> nullFuture = CompletableFuture.completedFuture(null);
            when(cacheService.getAsyncData(document)).thenReturn(Uni.createFrom().completionStage(nullFuture));

            // Mock DB query
            clientMock.when(() -> Client.findByDocument(document)).thenReturn(Uni.createFrom().item(dbClient));

            // Mock cache put
            when(cacheService.putAsyncData(eq(document), any(ClientCache.class)))
                    .thenReturn(Uni.createFrom().item(expectedCache));

            // Act
            Uni<Optional<ClientCache>> result = clientService.getClient(document);

            // Assert
            UniAssertSubscriber<Optional<ClientCache>> subscriber = result
                    .subscribe().withSubscriber(UniAssertSubscriber.create());
            
            subscriber.assertCompleted();
            ClientCache cached = subscriber.getItem()
                    .orElseThrow();
            assertThat(cached)
                    .extracting("document", "name")
                    .containsExactly(expectedCache.getDocument(), expectedCache.getName());
            verify(cacheService).getAsyncData(document);
            verify(cacheService).putAsyncData(eq(document), any(ClientCache.class));
        }
    }

    @Test
    @DisplayName("Debería retornar Optional vacío cuando el cliente no existe en caché ni en BD")
    void testGetClient_WhenNotInCacheAndNotInDb_ShouldReturnEmpty() {
        // Arrange
        String document = "99999";

        try (MockedStatic<Vertx> vertxMock = mockStatic(Vertx.class);
             MockedStatic<Client> clientMock = mockStatic(Client.class)) {
            
            vertxMock.when(Vertx::currentContext).thenReturn(context);
            doAnswer(invocation -> {
                Handler<Void> handler = invocation.getArgument(0);
                handler.handle(null);
                return null;
            }).when(context).runOnContext(any());

            // Mock cache returns null
            CompletableFuture<ClientCache> nullFuture = CompletableFuture.completedFuture(null);
            when(cacheService.getAsyncData(document)).thenReturn(Uni.createFrom().completionStage(nullFuture));

            // Mock DB query returns null
            clientMock.when(() -> Client.findByDocument(document)).thenReturn(Uni.createFrom().item((Client) null));

            // Act
            Uni<Optional<ClientCache>> result = clientService.getClient(document);

            // Assert
            UniAssertSubscriber<Optional<ClientCache>> subscriber = result
                    .subscribe().withSubscriber(UniAssertSubscriber.create());
            
            subscriber.assertCompleted();
            assertThat(subscriber.getItem()).isEmpty();
            verify(cacheService).getAsyncData(document);
        }
    }

    @Test
    @DisplayName("Debería manejar errores al obtener de la caché")
    void testGetClient_WhenCacheFails_ShouldPropagateError() {
        // Arrange
        String document = "12345";
        RuntimeException exception = new RuntimeException("Cache error");

        try (MockedStatic<Vertx> vertxMock = mockStatic(Vertx.class)) {
            vertxMock.when(Vertx::currentContext).thenReturn(context);
            doAnswer(invocation -> {
                Handler<Void> handler = invocation.getArgument(0);
                handler.handle(null);
                return null;
            }).when(context).runOnContext(any());

            when(cacheService.getAsyncData(document))
                    .thenReturn(Uni.createFrom().failure(exception));

            // Act
            Uni<Optional<ClientCache>> result = clientService.getClient(document);

            // Assert
            UniAssertSubscriber<Optional<ClientCache>> subscriber = result
                    .subscribe().withSubscriber(UniAssertSubscriber.create());
            
            subscriber.assertFailedWith(RuntimeException.class, "Cache error");
            verify(cacheService).getAsyncData(document);
        }
    }

    @Test
    @DisplayName("Debería manejar errores al consultar la base de datos")
    void testGetClient_WhenDbFails_ShouldPropagateError() {
        // Arrange
        String document = "12345";
        RuntimeException dbException = new RuntimeException("Database error");

        try (MockedStatic<Vertx> vertxMock = mockStatic(Vertx.class);
             MockedStatic<Client> clientMock = mockStatic(Client.class)) {
            
            vertxMock.when(Vertx::currentContext).thenReturn(context);
            doAnswer(invocation -> {
                Handler<Void> handler = invocation.getArgument(0);
                handler.handle(null);
                return null;
            }).when(context).runOnContext(any());

            // Mock cache returns null
            CompletableFuture<ClientCache> nullFuture = CompletableFuture.completedFuture(null);
            when(cacheService.getAsyncData(document)).thenReturn(Uni.createFrom().completionStage(nullFuture));

            // Mock DB query fails
            clientMock.when(() -> Client.findByDocument(document)).thenReturn(Uni.createFrom().failure(dbException));

            // Act
            Uni<Optional<ClientCache>> result = clientService.getClient(document);

            // Assert
            UniAssertSubscriber<Optional<ClientCache>> subscriber = result
                    .subscribe().withSubscriber(UniAssertSubscriber.create());
            
            subscriber.assertFailedWith(RuntimeException.class, "Database error");
            verify(cacheService).getAsyncData(document);
        }
    }

    @Test
    @DisplayName("Debería manejar errores al guardar en caché después de consultar BD")
    void testGetClient_WhenCachePutFails_ShouldPropagateError() {
        // Arrange
        String document = "12345";
        Client dbClient = createTestClient("12345", "CC", "John Doe", 
                "1234567890", "john@example.com", "123 Main St", "1234-5678-9012-3456");
        RuntimeException cacheException = new RuntimeException("Cache put error");

        try (MockedStatic<Vertx> vertxMock = mockStatic(Vertx.class);
             MockedStatic<Client> clientMock = mockStatic(Client.class)) {
            
            vertxMock.when(Vertx::currentContext).thenReturn(context);
            doAnswer(invocation -> {
                Handler<Void> handler = invocation.getArgument(0);
                handler.handle(null);
                return null;
            }).when(context).runOnContext(any());

            // Mock cache returns null
            CompletableFuture<ClientCache> nullFuture = CompletableFuture.completedFuture(null);
            when(cacheService.getAsyncData(document)).thenReturn(Uni.createFrom().completionStage(nullFuture));

            // Mock DB query succeeds
            clientMock.when(() -> Client.findByDocument(document)).thenReturn(Uni.createFrom().item(dbClient));

            // Mock cache put fails
            when(cacheService.putAsyncData(eq(document), any(ClientCache.class)))
                    .thenReturn(Uni.createFrom().failure(cacheException));

            // Act
            Uni<Optional<ClientCache>> result = clientService.getClient(document);

            // Assert
            UniAssertSubscriber<Optional<ClientCache>> subscriber = result
                    .subscribe().withSubscriber(UniAssertSubscriber.create());
            
            subscriber.assertFailedWith(RuntimeException.class, "Cache put error");
            verify(cacheService).getAsyncData(document);
            verify(cacheService).putAsyncData(eq(document), any(ClientCache.class));
        }
    }

    @Test
    @DisplayName("Debería crear la instancia correctamente con el constructor")
    void testConstructor_ShouldCreateInstanceWithCacheService() {
        // Arrange & Act
        ClientImpl clientImpl = new ClientImpl(cacheService);

        // Assert
        assertThat(clientImpl)
                .isNotNull()
                .isInstanceOf(ClientService.class);
    }

    @Test
    @DisplayName("Debería usar safeContext cuando el contexto actual es null")
    void testGetClient_WhenCurrentContextIsNull_ShouldUseSafeContext() {
        // Arrange
        String document = "12345";
        ClientCache cachedClient = createTestClientCache("12345", "CC", "John Doe", 
                "1234567890", "john@example.com", "123 Main St", "1234-5678-9012-3456");

        // Mock currentContext returns null, then safeContext creates a new one
        Context newContext = mock(Context.class);
        
        try (MockedStatic<Vertx> vertxMock = mockStatic(Vertx.class)) {
            Vertx mockVertx = mock(Vertx.class);
            vertxMock.when(Vertx::currentContext).thenReturn(null);
            vertxMock.when(Vertx::vertx).thenReturn(mockVertx);
            when(mockVertx.getOrCreateContext()).thenReturn(newContext);
            doAnswer(invocation -> {
                Handler<Void> handler = invocation.getArgument(0);
                handler.handle(null);
                return null;
            }).when(newContext).runOnContext(any());

            CompletableFuture<ClientCache> future = CompletableFuture.completedFuture(cachedClient);
            when(cacheService.getAsyncData(document)).thenReturn(Uni.createFrom().completionStage(future));

            // Act
            Uni<Optional<ClientCache>> result = clientService.getClient(document);

            // Assert
            UniAssertSubscriber<Optional<ClientCache>> subscriber = result
                    .subscribe().withSubscriber(UniAssertSubscriber.create());
            
            subscriber.assertCompleted();
            assertThat(subscriber.getItem()).isPresent();
        }
    }

    @Test
    @DisplayName("Debería convertir correctamente Client a ClientCache")
    void testToCache_ShouldConvertClientToClientCache() {
        // Arrange
        Client client = createTestClient("12345", "CC", "John Doe", 
                "1234567890", "john@example.com", "123 Main St", "1234-5678-9012-3456");
        ClientCache expectedCache = createTestClientCache("12345", "CC", "John Doe", 
                "1234567890", "john@example.com", "123 Main St", "1234-5678-9012-3456");

        try (MockedStatic<Vertx> vertxMock = mockStatic(Vertx.class);
             MockedStatic<Client> clientMock = mockStatic(Client.class)) {
            
            vertxMock.when(Vertx::currentContext).thenReturn(context);
            doAnswer(invocation -> {
                Handler<Void> handler = invocation.getArgument(0);
                handler.handle(null);
                return null;
            }).when(context).runOnContext(any());

            // Mock cache returns null
            CompletableFuture<ClientCache> nullFuture = CompletableFuture.completedFuture(null);
            when(cacheService.getAsyncData(anyString())).thenReturn(Uni.createFrom().completionStage(nullFuture));

            // Mock DB query
            clientMock.when(() -> Client.findByDocument(anyString())).thenReturn(Uni.createFrom().item(client));

            // Mock cache put
            when(cacheService.putAsyncData(anyString(), any(ClientCache.class)))
                    .thenReturn(Uni.createFrom().item(expectedCache));

            // Act
            Uni<Optional<ClientCache>> result = clientService.getClient("12345");

            // Assert
            UniAssertSubscriber<Optional<ClientCache>> subscriber = result
                    .subscribe().withSubscriber(UniAssertSubscriber.create());
            
            subscriber.assertCompleted();
            ClientCache cached = subscriber.getItem()
                    .orElseThrow();
            assertThat(cached)
                    .extracting("document", "documentType", "name", "phone", "email", "address", "creditCard")
                    .containsExactly(client.getDocument(), client.getDocumentType(), client.getName(), 
                            client.getPhone(), client.getEmail(), client.getAddress(), client.getCreditCard());
        }
    }

    @Test
    @DisplayName("Debería llamar findClientInDb correctamente")
    void testFindClientInDb_ShouldCallClientFindByDocument() {
        // Arrange
        String document = "12345";
        Client dbClient = createTestClient("12345", "CC", "John Doe", 
                "1234567890", "john@example.com", "123 Main St", "1234-5678-9012-3456");

        try (MockedStatic<Client> clientMock = mockStatic(Client.class)) {
            clientMock.when(() -> Client.findByDocument(document)).thenReturn(Uni.createFrom().item(dbClient));

            ClientImpl clientImpl = new ClientImpl(cacheService);

            // Act
            Uni<Client> result = clientImpl.findClientInDb(document);

            // Assert
            UniAssertSubscriber<Client> subscriber = result
                    .subscribe().withSubscriber(UniAssertSubscriber.create());
            
            subscriber.assertCompleted();
            assertThat(subscriber.getItem()).isEqualTo(dbClient);
        }
    }

    /**
     * Método auxiliar para crear instancias de ClientCache para pruebas.
     */
    private ClientCache createTestClientCache(String document, String documentType, 
            String name, String phone, String email, String address, String creditCard) {
        return new ClientCache(document, documentType, name, phone, email, address, creditCard);
    }

    /**
     * Método auxiliar para crear instancias de Client para pruebas.
     */
    private Client createTestClient(String document, String documentType, 
            String name, String phone, String email, String address, String creditCard) {
        return new Client(document, documentType, name, phone, email, address, creditCard);
    }
}
