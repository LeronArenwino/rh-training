package org.acme.resource.impl;

import static org.acme.utils.constants.Constants.NOT_FOUND;
import static org.acme.utils.constants.Constants.OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.acme.model.ClientCache;
import org.acme.resource.ClientResource;
import org.acme.services.ClientService;
import org.acme.utils.response.ResponseBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import jakarta.ws.rs.core.Response;

/**
 * Clase de pruebas unitarias para ClientImplement.
 * 
 * @author Felipe Malaver
 * @since 2025-12-09
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
class ClientImplementTest {

    @Mock
    private ClientService clientService;

    private ClientResource clientResource;

    @BeforeEach
    void setUp() {
        clientResource = new ClientImplement(clientService);
    }

    @Test
    @DisplayName("Debería retornar respuesta OK cuando el cliente se encuentra")
    void testGetClientByDocument_WhenClientFound_ShouldReturnOkResponse() {
        // Arrange
        String document = "12345";
        ClientCache clientCache = createTestClientCache("12345", "CC", "John Doe", 
                "1234567890", "john@example.com", "123 Main St", "1234-5678-9012-3456");

        when(clientService.getClient(document))
                .thenReturn(Uni.createFrom().item(Optional.of(clientCache)));

        // Act
        Uni<Response> result = clientResource.getClientByDocument(document);

        // Assert
        UniAssertSubscriber<Response> subscriber = result
                .subscribe().withSubscriber(UniAssertSubscriber.create());
        
        subscriber.assertCompleted();
        Response response = subscriber.getItem();
        
        assertThat(response)
                .isNotNull()
                .extracting(Response::getStatus, Response::getEntity)
                .containsExactly(OK, response.getEntity());
        assertThat(response.getEntity())
                .isNotNull()
                .isInstanceOf(ResponseBody.class);
        
        @SuppressWarnings("unchecked")
        ResponseBody<ClientCache> responseBody = (ResponseBody<ClientCache>) response.getEntity();
        assertThat(responseBody)
                .extracting(ResponseBody::getBody, 
                        r -> r.getHeader().getResponseCode(),
                        r -> r.getHeader().getResponseMessage())
                .containsExactly(clientCache, OK, "Cliente consultado exitosamente");
        
        verify(clientService).getClient(document);
    }

    @Test
    @DisplayName("Debería retornar respuesta NOT_FOUND cuando el cliente no se encuentra")
    void testGetClientByDocument_WhenClientNotFound_ShouldReturnNotFoundResponse() {
        // Arrange
        String document = "99999";

        when(clientService.getClient(document))
                .thenReturn(Uni.createFrom().item(Optional.empty()));

        // Act
        Uni<Response> result = clientResource.getClientByDocument(document);

        // Assert
        UniAssertSubscriber<Response> subscriber = result
                .subscribe().withSubscriber(UniAssertSubscriber.create());
        
        subscriber.assertCompleted();
        Response response = subscriber.getItem();
        
        assertThat(response)
                .isNotNull()
                .extracting(Response::getStatus, Response::getEntity)
                .containsExactly(NOT_FOUND, response.getEntity());
        assertThat(response.getEntity())
                .isNotNull()
                .isInstanceOf(ResponseBody.class);
        
        @SuppressWarnings("unchecked")
        ResponseBody<Object> responseBody = (ResponseBody<Object>) response.getEntity();
        assertThat(responseBody)
                .extracting(ResponseBody::getBody,
                        r -> r.getHeader().getResponseCode(),
                        r -> r.getHeader().getResponseMessage())
                .containsExactly(null, NOT_FOUND, "El cliente no fue encontrado o no existe");
        
        verify(clientService).getClient(document);
    }

    @Test
    @DisplayName("Debería propagar errores cuando el servicio falla")
    void testGetClientByDocument_WhenServiceFails_ShouldPropagateError() {
        // Arrange
        String document = "12345";
        RuntimeException exception = new RuntimeException("Service error");

        when(clientService.getClient(document))
                .thenReturn(Uni.createFrom().failure(exception));

        // Act
        Uni<Response> result = clientResource.getClientByDocument(document);

        // Assert
        UniAssertSubscriber<Response> subscriber = result
                .subscribe().withSubscriber(UniAssertSubscriber.create());
        
        subscriber.assertFailedWith(RuntimeException.class, "Service error");
        verify(clientService).getClient(document);
    }

    @Test
    @DisplayName("Debería crear la instancia correctamente con el constructor")
    void testConstructor_ShouldCreateInstanceWithClientService() {
        // Arrange & Act
        ClientImplement clientImplement = new ClientImplement(clientService);

        // Assert
        assertThat(clientImplement)
                .isNotNull()
                .isInstanceOf(ClientResource.class);
    }

    @Test
    @DisplayName("Debería manejar diferentes documentos correctamente")
    void testGetClientByDocument_WithDifferentDocuments_ShouldWorkCorrectly() {
        // Arrange
        String document1 = "11111";
        String document2 = "22222";
        ClientCache clientCache1 = createTestClientCache("11111", "CC", "Alice Smith", 
                "1111111111", "alice@example.com", "111 First St", "1111-1111-1111-1111");
        ClientCache clientCache2 = createTestClientCache("22222", "TI", "Bob Johnson", 
                "2222222222", "bob@example.com", "222 Second St", "2222-2222-2222-2222");

        when(clientService.getClient(document1))
                .thenReturn(Uni.createFrom().item(Optional.of(clientCache1)));
        when(clientService.getClient(document2))
                .thenReturn(Uni.createFrom().item(Optional.of(clientCache2)));

        // Act
        Uni<Response> result1 = clientResource.getClientByDocument(document1);
        Uni<Response> result2 = clientResource.getClientByDocument(document2);

        // Assert
        UniAssertSubscriber<Response> subscriber1 = result1
                .subscribe().withSubscriber(UniAssertSubscriber.create());
        UniAssertSubscriber<Response> subscriber2 = result2
                .subscribe().withSubscriber(UniAssertSubscriber.create());
        
        subscriber1.assertCompleted();
        subscriber2.assertCompleted();
        
        Response response1 = subscriber1.getItem();
        Response response2 = subscriber2.getItem();
        
        assertThat(response1.getStatus()).isEqualTo(OK);
        assertThat(response2.getStatus()).isEqualTo(OK);
        
        @SuppressWarnings("unchecked")
        ResponseBody<ClientCache> body1 = (ResponseBody<ClientCache>) response1.getEntity();
        @SuppressWarnings("unchecked")
        ResponseBody<ClientCache> body2 = (ResponseBody<ClientCache>) response2.getEntity();
        
        assertThat(body1.getBody().getDocument()).isEqualTo("11111");
        assertThat(body2.getBody().getDocument()).isEqualTo("22222");
        
        verify(clientService).getClient(document1);
        verify(clientService).getClient(document2);
    }

    @Test
    @DisplayName("Debería retornar respuesta correcta cuando el cliente tiene todos los campos")
    void testGetClientByDocument_WhenClientHasAllFields_ShouldReturnCompleteResponse() {
        // Arrange
        String document = "12345";
        ClientCache clientCache = createTestClientCache("12345", "CC", "John Doe", 
                "1234567890", "john@example.com", "123 Main St", "1234-5678-9012-3456");

        when(clientService.getClient(document))
                .thenReturn(Uni.createFrom().item(Optional.of(clientCache)));

        // Act
        Uni<Response> result = clientResource.getClientByDocument(document);

        // Assert
        UniAssertSubscriber<Response> subscriber = result
                .subscribe().withSubscriber(UniAssertSubscriber.create());
        
        subscriber.assertCompleted();
        Response response = subscriber.getItem();
        
        @SuppressWarnings("unchecked")
        ResponseBody<ClientCache> responseBody = (ResponseBody<ClientCache>) response.getEntity();
        ClientCache returnedClient = responseBody.getBody();
        
        assertThat(returnedClient)
                .extracting("document", "documentType", "name", "phone", "email", "address", "creditCard")
                .containsExactly("12345", "CC", "John Doe", "1234567890", 
                        "john@example.com", "123 Main St", "1234-5678-9012-3456");
    }

    /**
     * Método auxiliar para crear instancias de ClientCache para pruebas.
     */
    private ClientCache createTestClientCache(String document, String documentType, 
            String name, String phone, String email, String address, String creditCard) {
        return new ClientCache(document, documentType, name, phone, email, address, creditCard);
    }
}

