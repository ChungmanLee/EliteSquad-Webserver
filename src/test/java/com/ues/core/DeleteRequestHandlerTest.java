package com.ues.core;

import com.ues.database.ResourceManager;
import com.ues.http.HttpRequest;
import com.ues.http.HttpResponse;
import com.ues.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class DeleteRequestHandlerTest {

    private DeleteRequestHandler deleteRequestHandler;

    @BeforeEach
    void setUp() {
        deleteRequestHandler = new DeleteRequestHandler();
    }

    @Test
    void handle_deleteDataSuccess() {
        HttpRequest request = mock(HttpRequest.class);
        HttpResponse response = new HttpResponse();
        when(request.getPath()).thenReturn("/data/messages/1");

        try (MockedStatic<ResourceManager> mockedStatic = mockStatic(ResourceManager.class)) {
            mockedStatic.when(() -> ResourceManager.deleteData(anyString(), anyString()))
                    .thenReturn(Mono.just(true));

            Mono<Void> result = deleteRequestHandler.handle(request, response);

            StepVerifier.create(result)
                    .verifyComplete();

            assertEquals(HttpStatus.OK.getCode(), response.getStatusCode());
            assertEquals(HttpStatus.OK.getReasonPhrase(), response.getReasonPhrase());
            assertEquals("Data deleted successfully", new String(response.getBody()));
        }
    }

    @Test
    void handle_deleteDataNotFound() {
        HttpRequest request = mock(HttpRequest.class);
        HttpResponse response = new HttpResponse();
        when(request.getPath()).thenReturn("/data/messages/1");

        try (MockedStatic<ResourceManager> mockedStatic = mockStatic(ResourceManager.class)) {
            mockedStatic.when(() -> ResourceManager.deleteData(anyString(), anyString()))
                    .thenReturn(Mono.just(false));

            Mono<Void> result = deleteRequestHandler.handle(request, response);

            StepVerifier.create(result)
                    .verifyComplete();

            assertEquals(HttpStatus.NOT_FOUND.getCode(), response.getStatusCode());
            assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), response.getReasonPhrase());
            assertEquals("<h1>404 Not Found</h1><p>Data not found</p>", new String(response.getBody()));
        }
    }

    @Test
    void handle_deleteDataException() {
        HttpRequest request = mock(HttpRequest.class);
        HttpResponse response = new HttpResponse();
        when(request.getPath()).thenReturn("/data/messages/1");

        try (MockedStatic<ResourceManager> mockedStatic = mockStatic(ResourceManager.class)) {
            mockedStatic.when(() -> ResourceManager.deleteData(anyString(), anyString()))
                    .thenReturn(Mono.error(new RuntimeException("Database error")));

            Mono<Void> result = deleteRequestHandler.handle(request, response);

            StepVerifier.create(result)
                    .verifyComplete();

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.getCode(), response.getStatusCode());
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), response.getReasonPhrase());
            assertEquals("<h1>500 Internal Server Error</h1><p>Database error</p>", new String(response.getBody()));
        }
    }
}
