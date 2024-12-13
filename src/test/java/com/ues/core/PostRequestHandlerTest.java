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
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class PostRequestHandlerTest {

    private PostRequestHandler postRequestHandler;

    @BeforeEach
    void setUp() {
        postRequestHandler = new PostRequestHandler();
    }

    @Test
    void handle_createDataSuccess() {
        HttpRequest request = mock(HttpRequest.class);
        HttpResponse response = new HttpResponse();
        when(request.getPath()).thenReturn("/data/messages");
        when(request.getBody()).thenReturn("username=Chungman%20Lee&text=Hi");

        try (MockedStatic<ResourceManager> mockedStatic = mockStatic(ResourceManager.class)) {
            mockedStatic.when(() -> ResourceManager.createData(anyString(), anyMap()))
                    .thenReturn(Mono.just(true));

            Mono<Void> result = postRequestHandler.handle(request, response);

            StepVerifier.create(result)
                    .verifyComplete();

            assertEquals(HttpStatus.CREATED.getCode(), response.getStatusCode());
            assertEquals(HttpStatus.CREATED.getReasonPhrase(), response.getReasonPhrase());
            assertEquals("Data created successfully", new String(response.getBody()));
        }
    }

    @Test
    void handle_createDataConflict() {
        HttpRequest request = mock(HttpRequest.class);
        HttpResponse response = new HttpResponse();
        when(request.getPath()).thenReturn("/data/messages");
        when(request.getBody()).thenReturn("username=Chungman%20Lee&text=Hi");

        try (MockedStatic<ResourceManager> mockedStatic = mockStatic(ResourceManager.class)) {
            mockedStatic.when(() -> ResourceManager.createData(anyString(), anyMap()))
                    .thenReturn(Mono.just(false));

            Mono<Void> result = postRequestHandler.handle(request, response);

            StepVerifier.create(result)
                    .verifyComplete();

            assertEquals(HttpStatus.CONFLICT.getCode(), response.getStatusCode());
            assertEquals(HttpStatus.CONFLICT.getReasonPhrase(), response.getReasonPhrase());
            assertEquals("<h1>409 Conflict</h1><p>Failed to create data</p>", new String(response.getBody()));
        }
    }

    @Test
    void handle_createDataException() {
        HttpRequest request = mock(HttpRequest.class);
        HttpResponse response = new HttpResponse();
        when(request.getPath()).thenReturn("/data/messages");
        when(request.getBody()).thenReturn("username=Chungman%20Lee&text=Hi");

        try (MockedStatic<ResourceManager> mockedStatic = mockStatic(ResourceManager.class)) {
            mockedStatic.when(() -> ResourceManager.createData(anyString(), anyMap()))
                    .thenReturn(Mono.error(new RuntimeException("Database error")));

            Mono<Void> result = postRequestHandler.handle(request, response);

            StepVerifier.create(result)
                    .verifyComplete();

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.getCode(), response.getStatusCode());
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), response.getReasonPhrase());
            assertEquals("<h1>500 Internal Server Error</h1><p>Database error</p>", new String(response.getBody()));
        }
    }
}
