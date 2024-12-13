package com.ues.http;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class HttpRequestTest {

    @Test
    public void testRequestParsing() {
        // Given
        String request = "GET /index.html HTTP/1.1\r\n" +
                         "Host: example.com\r\n" +
                         "Connection: keep-alive\r\n" +
                         "\r\n" +
                         "body content";

        // When
        HttpRequest httpRequest = new HttpRequest(request);

        // Then
        assertEquals("GET", httpRequest.getMethod());
        assertEquals("/index.html", httpRequest.getPath());
        assertEquals("HTTP/1.1", httpRequest.getVersion());
        assertEquals("example.com", httpRequest.getHeader("Host"));
        assertEquals("keep-alive", httpRequest.getHeader("Connection"));
        assertEquals("body content", httpRequest.getBody());
    }

    @Test
    public void testRequestWithEmptyBody() {
        // Given
        String request = "POST /submit HTTP/1.1\r\n" +
                         "Content-Type: application/json\r\n" +
                         "Content-Length: 0\r\n" +
                         "\r\n";

        // When
        HttpRequest httpRequest = new HttpRequest(request);

        // Then
        assertEquals("POST", httpRequest.getMethod());
        assertEquals("/submit", httpRequest.getPath());
        assertEquals("HTTP/1.1", httpRequest.getVersion());
        assertEquals("application/json", httpRequest.getHeader("Content-Type"));
        assertEquals("0", httpRequest.getHeader("Content-Length"));
        assertEquals("", httpRequest.getBody());
    }

    @Test
    public void testRequestWithNoHeaders() {
        // Given
        String request = "GET /index.html HTTP/1.1\r\n" +
                         "\r\n" +
                         "body content";

        // When
        HttpRequest httpRequest = new HttpRequest(request);

        // Then
        assertEquals("GET", httpRequest.getMethod());
        assertEquals("/index.html", httpRequest.getPath());
        assertEquals("HTTP/1.1", httpRequest.getVersion());
        assertNull(httpRequest.getHeader("Host"));
        assertNull(httpRequest.getHeader("Connection"));
        assertEquals("body content", httpRequest.getBody());
    }

    @Test
    public void testRequestWithMultipleLinesInBody() {
        // Given
        String request = "POST /submit HTTP/1.1\r\n" +
                         "Content-Type: text/plain\r\n" +
                         "\r\n" +
                         "line 1\r\n" +
                         "line 2\r\n" +
                         "line 3";

        // When
        HttpRequest httpRequest = new HttpRequest(request);

        // Then
        assertEquals("POST", httpRequest.getMethod());
        assertEquals("/submit", httpRequest.getPath());
        assertEquals("HTTP/1.1", httpRequest.getVersion());
        assertEquals("text/plain", httpRequest.getHeader("Content-Type"));
        assertEquals("line 1\r\nline 2\r\nline 3", httpRequest.getBody());
    }
}
