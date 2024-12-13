package com.ues.http;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;

class HttpResponseUtilTest {

    @Test
    void testSend200() {
        HttpResponse response = new HttpResponse();
        String bodyContent = "OK";
        String contentType = "text/plain";

        Mono<Void> result = HttpResponseUtil.send200(response, bodyContent, contentType);

        assertEquals(Mono.empty(), result);
        assertEquals(200, response.getStatusCode());
        assertEquals("OK", response.getReasonPhrase());
        assertEquals(contentType, response.getHeaders().get("Content-Type"));
        assertArrayEquals(bodyContent.getBytes(), response.getBody());
    }

    @Test
    void testSend404() {
        HttpResponse response = new HttpResponse();
        String message = "Page not found";
        String contentType = "text/html";

        Mono<Void> result = HttpResponseUtil.send404(response, message, contentType);

        assertEquals(Mono.empty(), result);
        assertEquals(404, response.getStatusCode());
        assertEquals("Not Found", response.getReasonPhrase());
        assertEquals(contentType, response.getHeaders().get("Content-Type"));
        assertArrayEquals(("<h1>404 Not Found</h1><p>" + message + "</p>").getBytes(), response.getBody());
    }

    @Test
    void testSend405() {
        HttpResponse response = new HttpResponse();
        String contentType = "text/html";

        Mono<Void> result = HttpResponseUtil.send405(response, contentType);

        assertEquals(Mono.empty(), result);
        assertEquals(405, response.getStatusCode());
        assertEquals("Method Not Allowed", response.getReasonPhrase());
        assertEquals(contentType, response.getHeaders().get("Content-Type"));
        assertArrayEquals("<h1>405 Method Not Allowed</h1><p>The HTTP method is not allowed.</p>".getBytes(), response.getBody());
    }

    @Test
    void testSend500() {
        HttpResponse response = new HttpResponse();
        String message = "Internal server error";
        String contentType = "text/html";

        Mono<Void> result = HttpResponseUtil.send500(response, message, contentType);

        assertEquals(Mono.empty(), result);
        assertEquals(500, response.getStatusCode());
        assertEquals("Internal Server Error", response.getReasonPhrase());
        assertEquals(contentType, response.getHeaders().get("Content-Type"));
        assertArrayEquals(("<h1>500 Internal Server Error</h1><p>" + message + "</p>").getBytes(), response.getBody());
    }

    // Add more tests for other status codes and scenarios if needed
}
