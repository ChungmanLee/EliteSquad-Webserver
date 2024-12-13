package com.ues;

import com.ues.core.RequestHandler;
import com.ues.http.HttpRequest;
import com.ues.http.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import javax.net.ssl.SSLSocket;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SecureRequestHandlerTest {

    private SSLSocket sslSocket;
    private RequestHandler handler;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        sslSocket = mock(SSLSocket.class);
        handler = mock(RequestHandler.class);
        outputStream = new ByteArrayOutputStream();

        try {
            when(sslSocket.getOutputStream()).thenReturn(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testRun() throws IOException {
        // Prepare the input stream with a sample HTTP request
        String requestString = "GET / HTTP/1.1\r\nHost: example.com\r\n\r\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(requestString.getBytes());
        when(sslSocket.getInputStream()).thenReturn(inputStream);

        // Create a mock response
        HttpResponse mockResponse = new HttpResponse();
        mockResponse.setStatusCode(200);
        mockResponse.setReasonPhrase("OK");
        mockResponse.setHeaders(Map.of("Content-Length", "0"));
        mockResponse.setBody("".getBytes()); // Empty body for this example

        // Expected response bytes
        byte[] expectedResponseBytes = mockResponse.getResponseBytes();

        // Mock the handler's behavior
        when(handler.handle(any(HttpRequest.class), any(HttpResponse.class)))
                .thenAnswer(invocation -> {
                    HttpResponse response = invocation.getArgument(1);
                    response.setStatusCode(200);
                    response.setReasonPhrase("OK");
                    response.setHeaders(Map.of("Content-Length", "0"));
                    response.setBody("".getBytes());
                    return Mono.empty();
                });

        SecureRequestHandler secureRequestHandler = new SecureRequestHandler(sslSocket, handler);

        secureRequestHandler.run();

        // Get the actual response bytes from the output stream
        byte[] actualResponseBytes = outputStream.toByteArray();

        // Debugging output
        System.out.println("Expected Response Bytes: " + Arrays.toString(expectedResponseBytes));
        System.out.println("Actual Response Bytes: " + Arrays.toString(actualResponseBytes));

        // Compare the expected and actual response bytes
        assertArrayEquals(expectedResponseBytes, actualResponseBytes, "The response bytes do not match");

        // Verify SSL socket was closed
        verify(sslSocket).close();
    }

}
